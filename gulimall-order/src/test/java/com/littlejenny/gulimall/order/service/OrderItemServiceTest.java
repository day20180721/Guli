package com.littlejenny.gulimall.order.service;

import com.littlejenny.gulimall.order.entity.OrderItemEntity;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class OrderItemServiceTest {
    @Autowired
    RedisTemplate redisTemplate;
    @Test
    void save() {
        Shop shop = new Shop();
        shop.setName("Shop");
        shop.setSn(UUID.randomUUID().toString());
        redisTemplate.opsForValue().set("shop",shop);
        Object shopGet = redisTemplate.opsForValue().get("shop");
        System.out.println(shopGet);
    }
    @Data
    public static class Shop {
        private String name;
        private String sn;
    }
}