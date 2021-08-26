package com.littlejenny.common.exception;

public class RemoteServiceException extends RuntimeException {
    public RemoteServiceException(String msg){
        super("遠程調用服務失敗，訊息為:"+msg);
    }
}
