package com.littlejenny.gulimall.seckill;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableRedisHttpSession
@EnableRabbit
@EnableFeignClients
//下面兩個一組，Async是開啟異步任務，如果是同步↓
//每秒執行一個，但是方法卻要三秒完成就會變成每四秒執行一次
@EnableAsync
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GulimallSeckill18000Main {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSeckill18000Main.class, args);
    }

}
