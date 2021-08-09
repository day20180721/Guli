package com.littlejenny.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkufullReductionTO {
    private Long skuId;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer addOther;
}
