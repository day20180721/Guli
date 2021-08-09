package com.littlejenny.gulimall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GuliSearch13000Main {
    public static void main(String[] args) {
        SpringApplication.run(GuliSearch13000Main.class,args);
    }
}
