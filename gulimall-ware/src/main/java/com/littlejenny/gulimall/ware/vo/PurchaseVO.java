package com.littlejenny.gulimall.ware.vo;

import lombok.Data;

@Data
public class PurchaseVO {
    private Long purchaseId; //整单id
    private Long[] items;
}
