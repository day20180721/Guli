package com.littlejenny.common.to.coupon;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 滿多少折多少
 */
@Data
public class SkufullReductionTO {
    private Long skuId;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer addOther;
}
