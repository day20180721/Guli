package com.littlejenny.gulimall.ware.enums;

public enum OrderDetailState {
    LOCKED(1),
    UNLOCKED(2),
    SUBTRACT(3);


    private Integer code;
    private OrderDetailState(Integer code){
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
