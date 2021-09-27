package com.littlejenny.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.utils.R;
import com.littlejenny.common.constant.CartConstants;
import com.littlejenny.gulimall.cart.vo.CartVO;
import com.littlejenny.gulimall.cart.vo.CartItemVO;
import com.littlejenny.common.entity.VisitorLoginState;
import com.littlejenny.gulimall.cart.feign.ProductFeignService;
import com.littlejenny.gulimall.cart.interceptor.VisitorLoginStateInterceptor;
import com.littlejenny.gulimall.cart.service.CartService;
import com.littlejenny.gulimall.cart.to.SkuInfoTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ThreadPoolExecutor executor;
    @Override
    public CartItemVO itemTOcart(Long skuId, Integer count) throws ExecutionException, InterruptedException {
        CartItemVO cartItemBySkuId = new CartItemVO();
        //Get skuInfo
        CartItemVO finalCartItemBySkuId = cartItemBySkuId;
        CompletableFuture future = CompletableFuture.runAsync(()->{
            R r = productFeignService.skuInfo(skuId);
            SkuInfoTO info = r.getValue("skuInfo",new TypeReference<SkuInfoTO>() {
            });
            finalCartItemBySkuId.setCount(count);
            finalCartItemBySkuId.setImageUrl(info.getSkuDefaultImg());
            finalCartItemBySkuId.setTitle(info.getSkuTitle());
            finalCartItemBySkuId.setPrice(info.getPrice());
            finalCartItemBySkuId.setSkuId(info.getSkuId());
        },executor);
        //Get skuSaleAttrs SELECT * FROM gulimall_pms.pms_sku_sale_attr_value;
        CartItemVO finalCartItemBySkuId1 = cartItemBySkuId;
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            List<String> attrs = productFeignService.saleAttrsBySkuId(skuId);
            finalCartItemBySkuId1.setSkuAttr(attrs);
        },executor);
        CompletableFuture.allOf(future,future2).get();
        saveCartItem(cartItemBySkuId,true);
        return cartItemBySkuId;
    }
    public BoundHashOperations<String,String,String> getCartOps(){
        VisitorLoginState state = VisitorLoginStateInterceptor.visitorLoginState.get();
        String key = "";
        if(!StringUtils.isEmpty(state.getUserId())){
            key =getKey(state.getUserId());
//            key = state.getUserId();
        }else {
            key = getKey(state.getVisitorId());
//            key = state.getVisitorId();
        }
        return redisTemplate.boundHashOps(key);
    }

    /**
     * @param skuId
     * @param checked 0 = false | 1 = true
     */
    public void updateCartItemCheck(Long skuId,Integer checked){
        CartItemVO cartItemBySkuId = getCartItemBySkuId(skuId);
        cartItemBySkuId.setCheck(checked == 0 ? false : true);
        saveCartItem(cartItemBySkuId,false);
    }
    public void updateCartItemCount(Long skuId,Integer count){
        BoundHashOperations<String, String, String> cartOps = getCartOps();
        String itemJson = cartOps.get(skuId.toString());
        CartItemVO item = JSON.parseObject(itemJson, CartItemVO.class);
        item.setCount(count);
        saveCartItem(item,false);
    }
    public CartItemVO getCartItemBySkuId(Long skuId){
        BoundHashOperations<String, String, String> ops = getCartOps();
        String json = ops.get(skuId.toString());
        CartItemVO item = JSON.parseObject(json, CartItemVO.class);
        return item;
    }

    @Override
    public CartVO getCart() {
        VisitorLoginState state = VisitorLoginStateInterceptor.visitorLoginState.get();
        List<CartItemVO> allItems = new ArrayList<>();
        boolean userLogin = !StringUtils.isEmpty(state.getUserId());
        boolean visitorLogin = !StringUtils.isEmpty(state.getVisitorId());
        //1.遊客(O)用戶(X)只拿遊客購物車
        //2.遊客(O)用戶(O)遊客購物車的物品加到用戶購物車，在後再一次拿
        //先處理好遊客，最後在一次拿
        if(visitorLogin){
            List<CartItemVO> visitorItems = getItemsByMID(state.getVisitorId());
            if(visitorItems != null && visitorItems.size() > 0){
                //如果該用戶現在有登錄，並且以前有使用遊客將商品加入購物車，就要將遊客商品加入用戶的購物車
                if(userLogin){
                    saveCartItems(visitorItems,true);
                    removeCartAllItem(state.getVisitorId());
                }else {
                    allItems.addAll(visitorItems);
                }
            }
        }
        //如果用户登录就从Redis中把购物车项目拿出来
        if(userLogin){
            List<CartItemVO> userItems = getItemsByMID(state.getUserId());
            if(userItems != null && userItems.size() > 0)allItems.addAll(userItems);
        }
        CartVO cart = new CartVO();
        cart.setItems(allItems);
        return cart;
    }
    private String getKey(String id){
        return CartConstants.CART_PREFIX + id;
    }

    /**
     * 訪客登入後進入購物車需要合併購物項
     * @param id
     * @return
     */
    public List<CartItemVO> getItemsByMID(String id){
        List<CartItemVO> items = null;
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(getKey(id));
        List<Object> values = ops.values();
        if(values != null && values.size() > 0){
            items = values.stream().map(item ->{
                return JSON.parseObject((String) item, CartItemVO.class);
            }).collect(Collectors.toList());
        }
        return items;
    }
    public List<CartItemVO> getItemsByMIDNewestPrice(){
        List<CartItemVO> items = null;
        BoundHashOperations<String, String, String> ops = getCartOps();
        List<String> values = ops.values();
        if(values != null && values.size() > 0){
            items = values.stream().map(item ->{
                return JSON.parseObject(item, CartItemVO.class);
            }).collect(Collectors.toList());
            items = items.stream().filter(CartItemVO::getCheck).map(item ->{
                R r = productFeignService.skuInfo(item.getSkuId());
                SkuInfoTO info = r.getValue("skuInfo",new TypeReference<SkuInfoTO>() {
                });
                item.setPrice(info.getPrice());
                return item;
            }).collect(Collectors.toList());
        }
        return items;
    }
    public void saveCartItem(CartItemVO item,Boolean checkStack){
        BoundHashOperations<String, String, String> ops = getCartOps();
        if(checkStack){
            CartItemVO cartItemBySkuId = getCartItemBySkuId(item.getSkuId());
            if(cartItemBySkuId != null){
                cartItemBySkuId.addCount(item.getCount());
                String itemJson = JSON.toJSONString(cartItemBySkuId);
                ops.put(cartItemBySkuId.getSkuId().toString(),itemJson);
                return;
            }
        }
        String itemJson = JSON.toJSONString(item);
        ops.put(item.getSkuId().toString(),itemJson);
    }
    public void saveCartItems(List<CartItemVO> items,Boolean checkStack){
        //TODO 用户跟游客买的商品如有重复则要堆叠
        BoundHashOperations<String, String, String> ops = getCartOps();
        for (CartItemVO item : items) {
            saveCartItem(item,checkStack);
        }
//        Map<String, String> map = items.stream().collect(Collectors.toMap(item -> {
//            return item.getSkuId().toString();
//        }, item -> {
//            return JSON.toJSONString(item);
//        }));
//        ops.putAll(map);
    }
    public void removeCartItem(Long id){
        BoundHashOperations<String, String, String> ops = getCartOps();
        ops.delete(id.toString());
    }
    public void removeCartAllItem(String id){
        redisTemplate.delete(getKey(id));
    }
}
