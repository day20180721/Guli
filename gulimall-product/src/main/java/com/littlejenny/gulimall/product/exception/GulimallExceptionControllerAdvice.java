package com.littlejenny.gulimall.product.exception;

import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.common.utils.R;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
//下面為ControllerAdvice + responseBody合體
//@RestControllerAdvice(basePackages = {"com.littlejenny.gulimall.product.app","com.littlejenny.gulimall.product.web"})
//@ControllerAdvice(basePackages = "littlejenny.gulimall.auth.controller")
public class GulimallExceptionControllerAdvice {
    //可以返回modelAndView
    @ExceptionHandler(value = org.springframework.web.bind.MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        Map errorField = new HashMap();
        e.getBindingResult().getFieldErrors().forEach((item)->{
            //獲取到錯誤的提示
            String message = item.getDefaultMessage();
            //獲取到錯誤的屬性名稱
            String field = item.getField();
            errorField.put(field,message);
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMessage()).put("errorData",errorField);
    }
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable e){
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMessage());
    }
}
