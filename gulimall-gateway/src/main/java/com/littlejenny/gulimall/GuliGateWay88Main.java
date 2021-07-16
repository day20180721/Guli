package com.littlejenny.gulimall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GuliGateWay88Main {

    public static void main(String[] args) {
        SpringApplication.run(GuliGateWay88Main.class, args);
    }

}
