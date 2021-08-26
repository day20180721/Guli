package com.littlejenny.gulimall.cart.conf;

import com.littlejenny.gulimall.cart.interceptor.VisitorLoginStateInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebMVCConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VisitorLoginStateInterceptor()).addPathPatterns("/**");
    }
}
