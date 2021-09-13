package com.littlejenny.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.littlejenny.common.to.seckill.SeckillSessionAndSkuTO;
import com.littlejenny.gulimall.coupon.entity.SeckillSessionEntity;
import com.littlejenny.gulimall.coupon.service.SeckillSessionService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class SeskillTest {

    @Autowired
    private SeckillSessionServiceImpl seckillSessionService;
    @Test
    public void service() {
        SeckillSessionAndSkuTO sessionTOBeginEnd = seckillSessionService.getSessionTOBeginEnd(0L, 0L);
    }
}