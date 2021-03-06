package com.littlejenny.common.to.coupon;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPriceTO {
    private Long skuId;
    private Long memberLevelId;
    private String memberLevelName;
    private BigDecimal memberPrice;
}
