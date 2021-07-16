package com.littlejenny.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.littlejenny.gulimall.order.dao")
@SpringBootApplication
public class GuliOrder9000Main {
    public static void main(String[] args) {
        SpringApplication.run(GuliOrder9000Main.class,args);
    }
}
