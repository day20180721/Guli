package com.littlejenny.gulimall.order.service;

import com.littlejenny.gulimall.order.entity.OrderEntity;
import com.littlejenny.gulimall.order.service.impl.OrderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionTest {
    @Autowired
    private OrderServiceImpl orderService;

    @Test
    public void t(){
        a();
    }
    @Transactional
    public void a(){
        OrderEntity entity = new OrderEntity();
        entity.setMemberId(1L);
        orderService.save(entity);
        b();
        c();
        int i = 10 / 0;
    }
    @Transactional
    public void b(){
        OrderEntity entity = new OrderEntity();
        entity.setMemberId(2L);
        orderService.save(entity);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void c(){
        OrderEntity entity = new OrderEntity();
        entity.setMemberId(3L);
        orderService.save(entity);
    }
}
