package com.littlejenny.common.to.seckill;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillSkuRelationTO {
    private Long id;
    private Long promotionSessionId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private BigDecimal seckillCount;
    private BigDecimal seckillLimit;

    private Long beginTime;
    private Long endTime;
    private String token;

    private String skuName;
    private BigDecimal price;
    private String skuDefaultImg;
}
