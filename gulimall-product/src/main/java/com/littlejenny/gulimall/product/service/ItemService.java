package com.littlejenny.gulimall.product.service;

import com.littlejenny.gulimall.product.vo.item.ItemVO;

import java.util.concurrent.ExecutionException;

public interface ItemService {
    ItemVO item(Long skuId) throws ExecutionException, InterruptedException;
}
