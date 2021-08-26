package com.littlejenny.gulimall.cart.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuInfoTO {
    private Long skuId;
    private Long spuId;
    private String skuName;
    private String skuDesc;
    private Long catalogId;
    private Long brandId;
    private String skuDefaultImg;
    private String skuTitle;
    private String skuSubtitle;
    private BigDecimal price;
    private Long saleCount;
}
