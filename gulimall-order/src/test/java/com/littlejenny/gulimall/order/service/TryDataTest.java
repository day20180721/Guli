package com.littlejenny.gulimall.order.service;

import com.littlejenny.gulimall.order.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TryDataTest {
    @Autowired
    private OrderService orderService;
    @Transactional
    @Test
    void send() {
        try {
            OrderEntity entity = new OrderEntity();
            entity.setMemberId(5L);
            orderService.save(entity);
            int i = 10 / 0;
        }catch (Exception e){

        }
    }
}
