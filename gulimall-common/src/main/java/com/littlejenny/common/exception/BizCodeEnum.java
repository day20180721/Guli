package com.littlejenny.common.exception;

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系統未知異常"),
    VALID_EXCEPTION(10001,"參數格式驗證失敗"),
    ESUPSKU_EXCEPTION(11001,"ES上架SKU異常"),
    ELASTICIO_EXCEPTION(11000,"ES服務異常");
    private int code;
    private String message;
    private BizCodeEnum(int code,String message){
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
