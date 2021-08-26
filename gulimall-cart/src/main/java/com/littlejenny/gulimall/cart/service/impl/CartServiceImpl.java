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
        CartItemVO cartItemBySkuId = getCartItemBySkuId(skuId);
        if(cartItemBySkuId == null){
            cartItemBySkuId = new CartItemVO();
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

        }else {
            cartItemBySkuId.addCount(count);
        }
        saveCartItem(cartItemBySkuId);
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
        saveCartItem(cartItemBySkuId);
    }
    public void updateCartItemCount(Long skuId,Integer count){
        BoundHashOperations<String, String, String> cartOps = getCartOps();
        String itemJson = cartOps.get(skuId.toString());
        CartItemVO item = JSON.parseObject(itemJson, CartItemVO.class);
        item.setCount(count);
        saveCartItem(item);
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
        if(!StringUtils.isEmpty(state.getUserId())){
            List<CartItemVO> userItems = getItemsByMID(state.getUserId());
            if(userItems != null && userItems.size() > 0)allItems.addAll(userItems);
        }
        if(!StringUtils.isEmpty(state.getVisitorId())){
            List<CartItemVO> visitorItems = getItemsByMID(state.getVisitorId());
            if(visitorItems != null && visitorItems.size() > 0){
                allItems.addAll(visitorItems);
                saveCartItems(visitorItems);
                removeCartAllItem(state.getVisitorId());
            }
        }
        CartVO cart = new CartVO();
        cart.setItems(allItems);
        System.out.println(cart.getItems().size());
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
    public void saveCartItem(CartItemVO item){
        BoundHashOperations<String, String, String> ops = getCartOps();
        String itemJson = JSON.toJSONString(item);
        ops.put(item.getSkuId().toString(),itemJson);
    }
    public void saveCartItems(List<CartItemVO> items){
        BoundHashOperations<String, String, String> ops = getCartOps();
        Map<String, String> map = items.stream().collect(Collectors.toMap(item -> {
            return item.getSkuId().toString();
        }, item -> {
            return JSON.toJSONString(item);
        }));
        ops.putAll(map);
    }
    public void removeCartItem(Long id){
        BoundHashOperations<String, String, String> ops = getCartOps();
        ops.delete(id.toString());
    }
    public void removeCartAllItem(String id){
        redisTemplate.delete(getKey(id));
    }
}
