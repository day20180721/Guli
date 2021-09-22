package com.littlejenny.gulimall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//即使排除掉web包也要排除這個class，因為不確定其他的starter是否也有引用spring-boot-starter
//autoConfig是在spring-boot-starter
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GuliGateWay88Main {

    public static void main(String[] args) {
        SpringApplication.run(GuliGateWay88Main.class, args);
    }

}
