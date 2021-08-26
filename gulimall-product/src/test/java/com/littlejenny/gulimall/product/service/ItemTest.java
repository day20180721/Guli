package com.littlejenny.gulimall.product.service;

import com.littlejenny.gulimall.product.service.impl.ItemServiceImpl;
import com.littlejenny.gulimall.product.vo.item.ItemVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemTest {
    @Autowired
    private ItemServiceImpl itemService;

    @Test
    public void getAllSaleAttrBySpuId() throws ExecutionException, InterruptedException {
        ItemVO item = itemService.item(1L);
        System.out.println("END");
    }
}