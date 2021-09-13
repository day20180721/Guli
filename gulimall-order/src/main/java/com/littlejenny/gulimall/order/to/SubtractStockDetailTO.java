package com.littlejenny.gulimall.order.to;

import lombok.Data;

@Data
public class SubtractStockDetailTO {
    private Long skuId;
    private Integer quantity;
}
