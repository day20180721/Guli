package com.littlejenny.gulimall.auth.service;

import com.littlejenny.gulimall.auth.exception.SMSCDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

public interface RegistService {

    void sendCode(String phone)throws SMSCDException;
}
