package com.littlejenny.gulimall.auth.exception;

public class SMSCDException extends RuntimeException{
    public SMSCDException() {
        super("發送簡訊冷卻中");
    }
}
