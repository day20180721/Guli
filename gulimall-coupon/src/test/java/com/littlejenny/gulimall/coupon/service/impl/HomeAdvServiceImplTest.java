package com.littlejenny.gulimall.coupon.service.impl;

import com.littlejenny.gulimall.coupon.entity.HomeAdvEntity;
import com.littlejenny.gulimall.coupon.service.HomeAdvService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class HomeAdvServiceImplTest {
    @Autowired
    HomeAdvService homeAdvService;
    @Test
    public void save() {
        HomeAdvEntity homeAdvEntity = new HomeAdvEntity();
        homeAdvEntity.setName("測試");
        homeAdvService.save(homeAdvEntity);
    }
}