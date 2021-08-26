package com.littlejenny.gulimall.order.to;

import com.littlejenny.gulimall.order.entity.OrderEntity;
import com.littlejenny.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTO {
    private OrderEntity orderEntity;
    private List<OrderItemEntity> orderItemEntityList;
    private BigDecimal payPrice;
    private BigDecimal freight;

}
