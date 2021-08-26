package com.littlejenny.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.entity.VisitorLoginState;
import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.common.exception.OrderTokenInvalidException;
import com.littlejenny.common.exception.RemoteServiceException;
import com.littlejenny.common.exception.ware.NoWareCanHandleSkuException;
import com.littlejenny.common.exception.ware.SubTractStockException;
import com.littlejenny.common.to.HasStockTO;
import com.littlejenny.common.to.MemberEntityTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.order.constants.OrderConstants;
import com.littlejenny.gulimall.order.entity.OrderEntity;
import com.littlejenny.gulimall.order.entity.OrderItemEntity;
import com.littlejenny.gulimall.order.enums.OrderStatusEnum;
import com.littlejenny.gulimall.order.feign.*;
import com.littlejenny.gulimall.order.filter.UserFilter;
import com.littlejenny.gulimall.order.service.TradeService;
import com.littlejenny.gulimall.order.to.*;
import com.littlejenny.gulimall.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TradeServiceImpl implements TradeService {
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public OrderConfirmVO trade() throws ExecutionException, InterruptedException {
        OrderConfirmVO vo = new OrderConfirmVO();
        VisitorLoginState user = UserFilter.visitorLoginState.get();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        CompletableFuture addrFeature = CompletableFuture.runAsync(() -> {
            //List<MemberReceiveAddressVO> addrs;member List<MemberReceiveAddressEntity>
            R addrslistR = memberFeignService.attrsListByMId(Long.parseLong(user.getUserId()));
            List<MemberReceiveAddressVO> addrslist = addrslistR.getData(new TypeReference<List<MemberReceiveAddressVO>>() {
            });
            vo.setAddrs(addrslist);
        }, executor);
        CompletableFuture itemsFeature = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //List<CartItemVO> items; cart List<CartItemVO>
            //TODO 獲取到Redis中的購物車數據可能會有時價不同的問題，
            //所以真正在搜尋的時候還要去Sku表中在查一次真實數據
            R cartItemListR = cartFeignService.cartItems(Long.parseLong(user.getUserId()));
            List<CartItemVO> cartItemsList = cartItemListR.getData(new TypeReference<List<CartItemVO>>() {
            });
            cartItemsList = cartItemsList.stream().filter(CartItemVO::getCheck).collect(Collectors.toList());
            vo.setItems(cartItemsList);
        }, executor).thenRunAsync(() -> {
            //Map<String,Boolean> hasStockMap; ware
            List<CartItemVO> items = vo.getItems();
            List<Long> cartIdList = items.stream().map(CartItemVO::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.hasStockByIds(cartIdList);
            Map<Long, HasStockTO> itemStockMap = r.getData(new TypeReference<Map<Long, HasStockTO>>() {
            });
            vo.setHasStockMap(itemStockMap);
        }, executor);
        CompletableFuture memberFeature = CompletableFuture.runAsync(() -> {
            R memberInfoR = memberFeignService.memberInfo(Long.parseLong(user.getUserId()));
            MemberEntityTO memberInfo = memberInfoR.getValue("member", new TypeReference<MemberEntityTO>() {
            });
            vo.setIntegration(memberInfo.getIntegration());
        }, executor);
        //TODO 申請防重碼，key為前墜加上用戶編號
        String orderTokenKey = OrderConstants.ORDER_TOKEN_PREFIX + user.getUserId();
        String orderTokenValue = UUID.randomUUID().toString();
        redis.opsForValue().set(orderTokenKey, orderTokenValue, 5, TimeUnit.MINUTES);
        vo.setOrderSn(orderTokenValue);
        CompletableFuture.allOf(addrFeature, itemsFeature, memberFeature).get();
        return vo;
    }

    @Autowired
    private RedisTemplate redis;
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private OrderItemServiceImpl orderItemService;

    @Transactional
    @Override
    public SubmitOrderRespVO submitOrder(SubmitOrderVO vo)throws RuntimeException {
        /**
         * 驗證Token
         * 創建基本表單
         * 創建表單項
         * 匹配金額
         * 扣庫存
         */
        SubmitOrderRespVO respVO = new SubmitOrderRespVO();

        VisitorLoginState user = UserFilter.visitorLoginState.get();
        String orderTokenKey = OrderConstants.ORDER_TOKEN_PREFIX + user.getUserId();
        String script = "if redis.call ('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Long result = (Long) redis.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(orderTokenKey), vo.getOrderToken());
        if (result != 0) {
            OrderCreateTO orderCreateTO = new OrderCreateTO();
            buildOrder(vo, orderCreateTO, user.getUserId());
            buildOrderItems(orderCreateTO);
            buildTotalPay(orderCreateTO);
            buildTotalIntegration(orderCreateTO);
            //處理庫存，成功就生成訂單Item及訂單
            orderService.save(orderCreateTO.getOrderEntity());
            subStractStock(orderCreateTO);
            buildOrderItemOrderSn(orderCreateTO);
            orderItemService.saveBatch(orderCreateTO.getOrderItemEntityList());
            respVO.setOrder(orderCreateTO.getOrderEntity());
        } else {
            throw new OrderTokenInvalidException();
        }
        return respVO;
    }

    private void buildOrderItemOrderSn(OrderCreateTO orderCreateTO) {
        for (OrderItemEntity orderItemEntity : orderCreateTO.getOrderItemEntityList()) {
            orderItemEntity.setOrderId(orderCreateTO.getOrderEntity().getId());
        }
    }

    private void subStractStock(OrderCreateTO orderCreateTO)throws RemoteServiceException{
        List<SubtarctStockTO> subtarctStockTOS = orderCreateTO.getOrderItemEntityList().stream().map(item -> {
            SubtarctStockTO to = new SubtarctStockTO();
            to.setSkuId(item.getSkuId());
            to.setQuantity(item.getSkuQuantity());
            return to;
        }).collect(Collectors.toList());
        R r = wareFeignService.subTractStock(subtarctStockTOS);
        if (r.getCode() != 0) {
            throw new RemoteServiceException(r.getMsg());
        }
    }

    private void buildTotalIntegration(OrderCreateTO orderCreateTO) {
        BigDecimal totalIntegration = new BigDecimal(0);
        if(orderCreateTO.getOrderItemEntityList() == null || orderCreateTO.getOrderItemEntityList().size() == 0)return;
        for (OrderItemEntity orderItemEntity : orderCreateTO.getOrderItemEntityList()) {
            totalIntegration = totalIntegration.add(new BigDecimal(orderItemEntity.getGiftIntegration()));
        }
        orderCreateTO.getOrderEntity().setIntegration(totalIntegration.intValue());
    }

    /**
     * 最終支付金額
     * @param orderCreateTO
     */
    private void buildTotalPay(OrderCreateTO orderCreateTO) {
        BigDecimal totalPay = new BigDecimal(0);
        if(orderCreateTO.getOrderItemEntityList() == null || orderCreateTO.getOrderItemEntityList().size() == 0)return;
        for (OrderItemEntity orderItemEntity : orderCreateTO.getOrderItemEntityList()) {
            totalPay = totalPay.add(orderItemEntity.getRealAmount());
        }
        orderCreateTO.getOrderEntity().setTotalAmount(totalPay);
    }

    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private CouponFeignService couponFeignService;
    private void buildOrderItems(OrderCreateTO orderCreateTO) {
        R cartItemsNewest = cartFeignService.cartItemsNewest();
        List<CartItemVO> cartItemsList = cartItemsNewest.getData(new TypeReference<List<CartItemVO>>() {
        });
        if(cartItemsList == null || cartItemsList.size() == 0)return;
        List<OrderItemEntity> itemEntities = cartItemsList.stream().map(item -> {
            OrderItemEntity oIE = new OrderItemEntity();
            oIE.setOrderSn(orderCreateTO.getOrderEntity().getOrderSn());
            oIE.setSkuId(item.getSkuId());
            oIE.setSkuQuantity(item.getCount());
            oIE.setSkuPrice(item.getPrice());
            oIE.setSpuPic(item.getImageUrl());
            oIE.setRealAmount(oIE.getSkuPrice().multiply(new BigDecimal(oIE.getSkuQuantity())));

            String attrVals = String.join(";", item.getSkuAttr());
            oIE.setSkuAttrsVals(attrVals);

            R skuInfo = productFeignService.skuInfo(item.getSkuId());
            SkuInfoTO skuInfoTO = skuInfo.getValue("skuInfo", new TypeReference<SkuInfoTO>() {
            });
            oIE.setCategoryId(skuInfoTO.getCatalogId());
            oIE.setSpuBrand(skuInfoTO.getBrandId().toString());
            oIE.setSkuName(skuInfoTO.getSkuName());

//          TODO 我在Cart那邊已經查過一次Product了，既然都要查可以乾脆都在這邊查
//          TODO oIE.setRealAmount(); 要減去所有折價訊息
//          TODO  oIE.setOrderId();
//          查Spu 積分
            R r = couponFeignService.infoBySkuId(oIE.getSkuId());
            SpuBoundsTO spuBounds = r.getValue("spuBounds", new TypeReference<SpuBoundsTO>() {
            });
            oIE.setGiftIntegration(spuBounds.getBuyBounds().intValue());
            oIE.setGiftGrowth(spuBounds.getGrowBounds().intValue());
            return oIE;
        }).collect(Collectors.toList());
        orderCreateTO.setOrderItemEntityList(itemEntities);
    }

    private void buildOrder(SubmitOrderVO submit, OrderCreateTO order, String userId) {
        OrderEntity o = new OrderEntity();

        String uuid = IdWorker.get32UUID();
        o.setOrderSn(uuid);
        o.setFreightAmount(submit.getFreight());

        o.setCreateTime(new Date());
        o.setModifyTime(new Date());
        //member info
        R memberInfo = memberFeignService.memberInfo(Long.parseLong(userId));
        MemberEntityTO member = memberInfo.getValue("member", new TypeReference<MemberEntityTO>() {
        });
        o.setMemberId(member.getId());
        o.setMemberUsername(member.getUsername());

        //member Addr服務
        R addrinfo = memberFeignService.addrinfo(submit.getAddrID());
        MemberReceiveAddressEntityTO memberReceiveAddress = addrinfo.getValue("memberReceiveAddress", new TypeReference<MemberReceiveAddressEntityTO>() {
        });
        o.setReceiverName(memberReceiveAddress.getName());
        o.setReceiverPhone(memberReceiveAddress.getPhone());
        o.setReceiverCity(memberReceiveAddress.getCity());
        o.setReceiverDetailAddress(memberReceiveAddress.getDetailAddress());
        o.setReceiverProvince(memberReceiveAddress.getProvince());
        o.setReceiverRegion(memberReceiveAddress.getRegion());

        o.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

        order.setOrderEntity(o);
    }
}
