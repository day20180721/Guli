package com.littlejenny.common.to.coupon;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品贈送的會員經驗值、紅利
 */
@Data
public class SpuBoundsTO {
    private Long spuId;
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
}
