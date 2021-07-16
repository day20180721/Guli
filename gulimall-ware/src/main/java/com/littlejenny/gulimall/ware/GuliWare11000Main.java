package com.littlejenny.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.littlejenny.gulimall.ware.dao")
@SpringBootApplication
public class GuliWare11000Main {
    public static void main(String[] args) {
        SpringApplication.run(GuliWare11000Main.class,args);
    }
}
