package com.littlejenny.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundsTO {
    private Long spuId;
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
}
