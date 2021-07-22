package com.littlejenny.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.littlejenny.gulimall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class GuliProduct10000Main {
    public static void main(String[] args) {
        SpringApplication.run(GuliProduct10000Main.class,args);
    }
}
