package com.littlejenny.gulimall.cart.service;

import com.littlejenny.gulimall.cart.vo.CartVO;
import com.littlejenny.gulimall.cart.vo.CartItemVO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItemVO itemTOcart(Long skuId, Integer count) throws ExecutionException, InterruptedException;
    CartItemVO getCartItemBySkuId(Long skuId);
    CartVO getCart();
    void updateCartItemCheck(Long skuId,Integer checked);
    void updateCartItemCount(Long skuId,Integer count);
    void removeCartItem(Long id);

    List<CartItemVO> getItemsByMID(String id);
}
