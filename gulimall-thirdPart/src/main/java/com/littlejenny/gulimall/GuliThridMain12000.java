package com.littlejenny.gulimall;

import com.azure.spring.autoconfigure.storage.StorageAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {StorageAutoConfiguration.class,DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class GuliThridMain12000 {
    public static void main(String[] args) {
        SpringApplication.run(GuliThridMain12000.class, args);
    }

}
//https://gulimall.blob.core.windows.net/brandicon/2021-07-27/EV7faPsUcAA_vM3.jpg
//https://gulimall.blob.core.windows.net/brandicon/2021-07-27/
