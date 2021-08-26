package com.littlejenny.gulimall.product.service.impl;

import com.littlejenny.gulimall.product.entity.SkuImagesEntity;
import com.littlejenny.gulimall.product.entity.SkuInfoEntity;
import com.littlejenny.gulimall.product.service.ItemService;
import com.littlejenny.gulimall.product.vo.item.ItemVO;
import com.littlejenny.gulimall.product.vo.item.SkuAttrGroupVO;
import com.littlejenny.gulimall.product.vo.item.SpuAttrGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private SkuInfoServiceImpl skuInfoService;
    @Autowired
    private AttrServiceImpl attrService;
    @Autowired
    private SkuImagesServiceImpl skuImagesService;
    @Autowired
    private SpuInfoDescServiceImpl spuInfoDescService;
    @Autowired
    private ThreadPoolExecutor pool;
    @Override
    public ItemVO item(Long skuId) throws ExecutionException, InterruptedException {
        ItemVO itemVO = new ItemVO();
        CompletableFuture<SkuInfoEntity> setSkuInfoEntity = CompletableFuture.supplyAsync(()->{
            //SKU基本訊息
            SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
            itemVO.setSkuInfoEntity(skuInfo);
            return skuInfo;
        },pool);
        CompletableFuture<Void> setSkuAttrGroupVOS = setSkuInfoEntity.thenAcceptAsync(item -> {
            //SKU的SPU所有銷售訊息
            Long spuId = item.getSpuId();
            List<SkuAttrGroupVO> skuAttrGroupVOS = attrService.getAllSaleAttrBySpuId(spuId);
            itemVO.setSkuAttrGroupVOS(skuAttrGroupVOS);
        },pool);
        CompletableFuture<Void> setSpuInfoDescEntities = setSkuInfoEntity.thenAcceptAsync(item -> {
            //SPU所有商品介紹圖片
            Long spuId = item.getSpuId();
            List<String> spuInfoDescEntities = spuInfoDescService.getBySpuId(spuId);
            itemVO.setSpuInfoDescEntities(spuInfoDescEntities);
        }, pool);
        CompletableFuture<Void> setSpuAttrGroupVOS = setSkuInfoEntity.thenAcceptAsync(item -> {
            //SPU的基本屬性
            Long catalogId = item.getCatalogId();
            Long spuId = item.getSpuId();
            List<SpuAttrGroupVO> spuAttrGroupVOS = attrService.getAllAttrContainGroupByCatlogIdAndSpuId(catalogId, spuId);
            itemVO.setSpuAttrGroupVOS(spuAttrGroupVOS);

        }, pool);

        CompletableFuture<Void> setSkuImagesEntities = CompletableFuture.runAsync(() -> {
            //該SKU使用的商品圖片
            List<SkuImagesEntity> skuImagesEntityList = skuImagesService.getBySkuId(skuId);
            itemVO.setSkuImagesEntities(skuImagesEntityList);
        }, pool);
        CompletableFuture.allOf(setSkuAttrGroupVOS,setSpuAttrGroupVOS,setSpuInfoDescEntities,setSkuImagesEntities).get();
        return itemVO;
    }
}
