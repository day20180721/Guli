package com.littlejenny.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuLadderTO {
    private Long skuId;
    private Integer fullCount;
    private BigDecimal discount;
    private BigDecimal price;
    private Integer addOther;
}
