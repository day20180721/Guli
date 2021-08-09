package com.littlejenny.gulimall.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;

//@Configuration
public class CustomConfig {
    @Bean
    public ServerCodecConfigurer serverCodecConfigurer(){
        return ServerCodecConfigurer.create();
    }
}
