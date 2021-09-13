package com.littlejenny.common.exception;

/**
 * 11 ware
 * 1 system
 * 10 product
 * 8 member
 * 15 auth
 * 9 order
 */

public enum BizCodeEnum {
    TOOMUCH_REQUEST_EXCEPTION(880,"請求次數過多"),
    UNKNOW_EXCEPTION(1000,"系統未知異常"),
    VALID_EXCEPTION(1001,"參數格式驗證失敗"),
    REMOTESERVICE_EXCEPTION(1002,"遠程服務調用失敗"),
    ESUPSKU_EXCEPTION(10001,"ES上架SKU異常"),
    ELASTICIO_EXCEPTION(10000,"ES服務異常"),
    SAMEACCOUNT_EXCEPTION(8001,"此帳號已經使用"),
    OAUTHLOGIN_EXCEPTION(8002,"社交登入失敗"),
    ORDERTOKEN_INVALID_EXCEPTION(9001,"訂單識別碼錯誤"),
    LOGIN_EXCEPTION(8003,"帳號或密碼錯誤"),
    SMSCD_EXCEPTION(15001,"簡訊服務冷卻中"),
    SUBTRACTSTOCK_EXCEPTION(11001,"減去庫存錯誤"),
    NOWARECANHANDLE_EXCEPTION(11002,"沒有任何倉庫有足夠庫存");
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
