package com.littlejenny.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.littlejenny.gulimall.coupon.dao")
@SpringBootApplication
public class GuliCoupon7000Main {
    public static void main(String[] args) {
        SpringApplication.run(GuliCoupon7000Main.class,args);
    }
}
