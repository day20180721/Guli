package com.littlejenny.gulimall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.entity.VisitorLoginState;
import com.littlejenny.common.enums.order.OrderStatusEnum;
import com.littlejenny.common.exception.OrderTokenInvalidException;
import com.littlejenny.common.exception.RemoteServiceException;
import com.littlejenny.common.to.ware.HasStockTO;
import com.littlejenny.common.to.member.MemberEntityTO;
import com.littlejenny.common.to.ware.WareOrderTaskTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.order.constants.OrderConstants;
import com.littlejenny.gulimall.order.entity.OrderEntity;
import com.littlejenny.gulimall.order.entity.OrderItemEntity;
import com.littlejenny.gulimall.order.entity.PaymentInfoEntity;
import com.littlejenny.gulimall.order.enums.PayType;
import com.littlejenny.gulimall.order.enums.PaymentState;
import com.littlejenny.gulimall.order.feign.*;
import com.littlejenny.gulimall.order.filter.UserFilter;
import com.littlejenny.gulimall.order.service.OrderService;
import com.littlejenny.gulimall.order.service.PaymentInfoService;
import com.littlejenny.gulimall.order.service.TradeService;
import com.littlejenny.gulimall.order.to.*;
import com.littlejenny.gulimall.order.vo.*;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
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

    /**
     * ?????????????????????????????????????????????????????????
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
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
            //?????????Redis?????????????????????????????????????????????????????????
            //???????????????????????????????????????Sku??????????????????????????????
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
        //??????????????????key???????????????????????????
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
         * ??????Token
         * ??????????????????
         * ???????????????
         * ????????????
         * ?????????
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
            //????????????????????????????????????Item?????????
            orderService.save(orderCreateTO.getOrderEntity());

            //???????????????
            WareOrderTaskTO wareOrderTaskTO = new WareOrderTaskTO();
            wareOrderTaskTO.setOrderSn(orderCreateTO.getOrderEntity().getOrderSn());
            wareFeignService.saveTaskTO(wareOrderTaskTO);

            subStractStock(orderCreateTO,orderCreateTO.getOrderEntity().getOrderSn());
            buildOrderItemOrderSn(orderCreateTO);
            orderItemService.saveBatch(orderCreateTO.getOrderItemEntityList());
            respVO.setOrder(orderCreateTO.getOrderEntity());
        } else {
            throw new OrderTokenInvalidException();
        }
        return respVO;
    }

    @Autowired
    private PaymentInfoService paymentInfoService;
    private String clientId = "Ac3UabNiB0ctYzFrNq4LlKOMrTGITss94DTo0FcfqA38MZ4buJJ8UK3S9L7NRPsQGwEksZ6OscV0Jv1V";
    private String clientSecret = "EEy7qbObCKnI1H4UJl2AropC6k2iIMexCEY-Vl_jHLzqIJufPwsL5vwS_Zgy0C-kOIt6vhypj0TmZMlA";

    private APIContext context = new APIContext(clientId, clientSecret, "sandbox");
    @Override
    public String payByPaypal(String sn) {
        OrderEntity bySn = orderService.getBySn(sn);
        // Set payer details
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

// Set redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://order.gulimall.com/paypal/cancel");
        redirectUrls.setReturnUrl("http://order.gulimall.com/paypal/process");

// Set payment details
//        Details details = new Details();
//        details.setShipping(bySn.getFreightAmount().setScale(2, RoundingMode.UP).toString());
//        details.setSubtotal(bySn.getTotalAmount().setScale(2, RoundingMode.UP).toString());

// Payment amount
        Amount amount = new Amount();
        amount.setCurrency("USD");

// Total must be equal to sum of shipping, tax and subtotal.
        BigDecimal actualPay = bySn.getFreightAmount().add(bySn.getTotalAmount());
        //TODO ?????????????????????????????????????????????5
//        amount.setTotal(actualPay.setScale(2,RoundingMode.FLOOR).toString());
        amount.setTotal("5");
//        amount.setDetails(details);

// Transaction information
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction
                .setDescription("This is the payment transaction description.");

// Add transaction to a list
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

// Add payment details
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactions);


        try {
            Payment createdPayment = payment.create(context);

            OrderEntity order = orderService.getBySn(sn);
            order.setPayType(PayType.PAYPAL.getCode());
            orderService.updateById(order);
            //??????paypal??????
            //?????????RabbitMQ????????????
            PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
            paymentInfoEntity.setOrderSn(sn);
            paymentInfoEntity.setTotalAmount(new BigDecimal(createdPayment.getTransactions().get(0).getAmount().getTotal()));
            paymentInfoEntity.setPaymentId(createdPayment.getId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date createDate = sdf.parse(createdPayment.getCreateTime());
            paymentInfoEntity.setCreateTime(createDate);
            paymentInfoEntity.setPaymentStatus(PaymentState.CREATED.toString());
            paymentInfoEntity.setOrderId(order.getId());
            //#??????????????????????????????????????????????????????????????????
            //??????????????????????????????????????????????????????URL?????????????????????????????????????????????????????????Key???Authorization Value???:Basic??????(ClientID +""+ Secret???GenerateBase64String??????)

            paymentInfoEntity.setCallbackContent(createdPayment.getLinks().get(1).getHref());
            paymentInfoService.save(paymentInfoEntity);

            Iterator links = createdPayment.getLinks().iterator();
            while (links.hasNext()) {
                Links link = (Links) links.next();
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    String href = link.getHref();
                    // Redirect the customer to link.getHref()
                    return href;
                }
            }
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        } catch (ParseException e ){
            System.out.println(e.getMessage());
        }
        return "";
    }
    //TODO ??????????????????paypal???????????????
    //??????rabbitMQ????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public void processPaypal(String paymentId, String payerID) {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerID);
        try {
            Payment createdPayment = payment.execute(context, paymentExecution);
            //?????????????????????????????????????????????????????????
            PaymentInfoEntity payInfo = paymentInfoService.getByPayID(paymentId);
            payInfo.setPaymentStatus(PaymentState.CAPTURE.name());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date createDate = sdf.parse(createdPayment.getCreateTime());
            payInfo.setConfirmTime(createDate);

            paymentInfoService.updateById(payInfo);

            String orderSn = payInfo.getOrderSn();
            OrderEntity order = orderService.getBySn(orderSn);
            order.setStatus(OrderStatusEnum.PAYED.getCode());
            order.setModifyTime(new Date());
            orderService.updateById(order);
        } catch (PayPalRESTException e) {
            //TODO ?????????????????????MQ??????SQL??????
            System.err.println(e.getDetails());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void buildOrderItemOrderSn(OrderCreateTO orderCreateTO) {
        for (OrderItemEntity orderItemEntity : orderCreateTO.getOrderItemEntityList()) {
            orderItemEntity.setOrderId(orderCreateTO.getOrderEntity().getId());
        }
    }

    //TODO ????????????????????????SQL????????????????????????????????????????????????????????????Where???????????????
    //?????????????????????1?????????????????????????????????>=1??????-1?????????????????????-1?????????
    //??????????????????????????????????????????????????????????????????
    //?????????:????????????????????????1????????????????????????SQL?????????SQL??????????????????Where
    private void subStractStock(OrderCreateTO orderCreateTO,String orderSn)throws RemoteServiceException{
        List<SubtractStockDetailTO> subtarctStockTOS = orderCreateTO.getOrderItemEntityList().stream().map(item -> {
            SubtractStockDetailTO to = new SubtractStockDetailTO();
            to.setSkuId(item.getSkuId());
            to.setQuantity(item.getSkuQuantity());
            return to;
        }).collect(Collectors.toList());
        //????????????????????????task????????????
        //??????????????????????????????taskID????????????orderSn
        //????????????????????????message???????????????orderSn????????????????????????????????????
        //??????????????????taskID??????orderSn
        //???????????????????????????task???detail?????????????????????key -> orderSn
        SubtarctStockTO subtarctStockTO = new SubtarctStockTO();
        subtarctStockTO.setOrderSn(orderSn);
        subtarctStockTO.setDetailTOList(subtarctStockTOS);
        R r = wareFeignService.lockStocks(subtarctStockTO);
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
     * ??????????????????
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

            // TODO ??????Cart????????????????????????Product????????????????????????????????????????????????
            //oIE.setRealAmount(); ???????????????????????????

//          ???Spu ??????
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

        //member Addr??????
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
