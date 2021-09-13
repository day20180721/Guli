package com.littlejenny.gulimall.rabbitmq;
import com.littlejenny.gulimall.rabbitmq.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AutoWareTest {

    @Autowired
    User u1;
    @Autowired
    User u2;

    @Test
    public void getAllSaleAttrBySpuId() {
        u1.setName("77");
        System.out.println(u2);

    }
}