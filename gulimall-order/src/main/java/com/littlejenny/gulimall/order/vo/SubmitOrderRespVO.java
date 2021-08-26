package com.littlejenny.gulimall.order.vo;

import com.littlejenny.gulimall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderRespVO {
    private OrderEntity order;
    private Integer code = 0;
}
