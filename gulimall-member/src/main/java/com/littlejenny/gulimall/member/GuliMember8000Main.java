package com.littlejenny.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.littlejenny.gulimall.member.dao")
@SpringBootApplication
public class GuliMember8000Main {
    public static void main(String[] args) {
        SpringApplication.run(GuliMember8000Main.class,args);
    }
}
