package com.littlejenny.gulimall.seckill;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootTest
class SemaphoreTests {
    @Autowired
    private RedissonClient client;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("8","5");
        RSemaphore semaphore1 = client.getSemaphore("8");
        System.out.println(semaphore1.tryAcquire(3));
        RSemaphore semaphore = client.getSemaphore("7");
        System.out.println(semaphore.tryAcquire(3));
    }

}
