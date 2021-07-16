package com.littlejenny.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.littlejenny.gulimall.product.dao")
@SpringBootApplication
public class GuliProduct10000Main {
    public static void main(String[] args) {
        SpringApplication.run(GuliProduct10000Main.class,args);
    }
}
