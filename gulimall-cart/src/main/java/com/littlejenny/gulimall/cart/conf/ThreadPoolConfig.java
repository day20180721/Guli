package com.littlejenny.gulimall.cart.conf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(value = {ThreadPoolProperties.class})
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties properties){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(properties.getCore(), properties.getAddition(),
                properties.getLeaveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(properties.getBlockingQueue()),Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }
}
