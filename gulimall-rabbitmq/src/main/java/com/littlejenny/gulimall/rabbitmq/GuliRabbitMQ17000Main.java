package com.littlejenny.gulimall.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GuliRabbitMQ17000Main {
    public static void main(String[] args) {
        SpringApplication.run(GuliRabbitMQ17000Main.class,args);
    }
}
