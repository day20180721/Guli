package com.littlejenny.gulimall.order.service;

import com.littlejenny.gulimall.order.entity.OrderItemEntity;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class OrderItemServiceTest {
    @Autowired
    OrderItemService service;
    @Test
    void save() {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn("7777");
        service.save(orderItemEntity);
    }
}