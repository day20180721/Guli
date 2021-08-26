package com.littlejenny.common.exception;

public class OrderTokenInvalidException extends RuntimeException{
    public OrderTokenInvalidException() {
        super("訂單標誌錯誤");
    }
}
