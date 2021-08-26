package com.littlejenny.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubmitOrderVO {
    private Long addrID;
    private BigDecimal freight;
    private BigDecimal realPay;
    private String orderToken;
}
