package com.littlejenny.gulimall.auth.exceptionadvice;

import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//@Slf4j
//@RestControllerAdvice(basePackages = "com.littlejenny.gulimall.auth")
public class GulimallExceptionControllerAdvice {
//    @ExceptionHandler(value = Throwable.class)
//    public R handleException(Throwable e){
//        log.error(e.getMessage());
//        e.printStackTrace();
//        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMessage());
//    }
}
