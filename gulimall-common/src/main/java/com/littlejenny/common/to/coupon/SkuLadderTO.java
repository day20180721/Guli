package com.littlejenny.common.to.coupon;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 滿多少打多少折，price為計算出來的值
 */
@Data
public class SkuLadderTO {
    private Long skuId;
    private Integer fullCount;
    private BigDecimal discount;
    private BigDecimal price;
    private Integer addOther;
}
