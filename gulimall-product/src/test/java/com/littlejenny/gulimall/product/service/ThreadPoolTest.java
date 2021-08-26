package com.littlejenny.gulimall.product.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ThreadPoolExecutor;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ThreadPoolTest {
    @Autowired
    private ThreadPoolExecutor itemService;

    @Test
    public void getAllSaleAttrBySpuId() {
        System.out.println(itemService);
    }
}
