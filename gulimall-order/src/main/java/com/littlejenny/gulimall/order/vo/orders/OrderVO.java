package com.littlejenny.gulimall.order.vo.orders;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVO {
    private String orderSn;
    private Date createTime;
    private BigDecimal totalAmount;
    private Integer status;
    private Integer payType;
    private String receiverName;
    List<OrderItemVO> items;
}
