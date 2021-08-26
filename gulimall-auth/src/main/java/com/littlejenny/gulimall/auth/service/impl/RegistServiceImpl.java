package com.littlejenny.gulimall.auth.service.impl;

import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.auth.constants.AuthConstants;
import com.littlejenny.gulimall.auth.exception.SMSCDException;
import com.littlejenny.gulimall.auth.feign.ThridPartFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class RegistServiceImpl {
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private ThridPartFeignService thridPartFeignService;
    public boolean matchCode(String phone,String code){
        Boolean hasKey = redis.hasKey(AuthConstants.REDIS_PREFIX + phone);
        if(!hasKey)return false;
        String value = redis.opsForValue().get(AuthConstants.REDIS_PREFIX + phone);
        String[] splitValue = value.split("_");
        return splitValue[0].equals(code);
    }
    public void removeCode(String phone){
        redis.delete(AuthConstants.REDIS_PREFIX + phone);
    }
    public void sendCode(String phone) throws SMSCDException{
        String msg = "0204";
        long currentTimeMillis = System.currentTimeMillis();
        Boolean hasKey = redis.hasKey(AuthConstants.REDIS_PREFIX + phone);
        if(hasKey){
            String value = redis.opsForValue().get(AuthConstants.REDIS_PREFIX + phone);
            String[] splitValue = value.split("_");
            long timeOffset = currentTimeMillis - Long.parseLong(splitValue[1]);
            if(timeOffset < AuthConstants.SMS_TIMEOUT ){
                throw new SMSCDException();
            }
        }
        //TODO 紀錄此MSG到Redis
        redis.opsForValue().set(AuthConstants.REDIS_PREFIX + phone,msg + "_" + currentTimeMillis,AuthConstants.SMS_TIMEOUT, TimeUnit.MILLISECONDS);
        //TODO 調用簡訊服務
        R r = thridPartFeignService.sendSms(phone, msg);
        if(r.getCode() != 0){
            //TODO 拋出相關異常
        }
    }
}
