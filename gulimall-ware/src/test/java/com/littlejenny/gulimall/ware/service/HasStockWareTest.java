package com.littlejenny.gulimall.ware.service;

import com.littlejenny.gulimall.ware.dao.WareSkuDao;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class HasStockWareTest {
    @Autowired
    WareSkuDao dao;
    @Test
    void save() {
        System.out.println(dao.getHasStockWareBySKuID(1L, 43));
    }
}