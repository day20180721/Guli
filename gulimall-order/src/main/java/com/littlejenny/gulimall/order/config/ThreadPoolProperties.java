package com.littlejenny.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread")
@Data
public class ThreadPoolProperties {
    private Integer core;
    private Integer addition;
    private Long leaveTime;
    private Integer blockingQueue;
}
