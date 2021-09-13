package com.littlejenny.gulimall.order.enums;

import lombok.Data;

public enum PayType {
    PAYPAL(1,"PAYPAL");
    private Integer code;
    private String msg;
    private PayType(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
