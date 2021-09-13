package com.littlejenny.gulimall.order.to;

import lombok.Data;

import java.util.List;

@Data
public class SubtarctStockTO {
    private String orderSn;
    List<SubtractStockDetailTO> detailTOList;
}
