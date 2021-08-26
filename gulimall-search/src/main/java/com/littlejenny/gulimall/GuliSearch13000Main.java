package com.littlejenny.gulimall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class GuliSearch13000Main {
    public static void main(String[] args) {
        SpringApplication.run(GuliSearch13000Main.class,args);
    }
}
