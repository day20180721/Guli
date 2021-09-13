package com.littlejenny.gulimall.order.vo.orders;

import lombok.Data;

@Data
public class OrderItemVO {
    private String skuName;
    private Integer skuQuantity;
    private String skuPic;
}
