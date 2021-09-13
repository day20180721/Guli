package com.littlejenny.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.littlejenny.gulimall.coupon.entity.HomeAdvEntity;
import com.littlejenny.gulimall.coupon.entity.SeckillSessionEntity;
import com.littlejenny.gulimall.coupon.service.HomeAdvService;
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
class DateTest {

    @Autowired
    SeckillSessionService service;
    @Autowired
    private SeckillSessionService seckillSessionService;
    //我存CST標準時間24點到SQL會變成18點
    //意思是存進SQL的過程他會辨認時區並且改成他所相對應的時區
    //所以要改SQL為中原標準時間
    @Test
    public void t(){
        //純粹的數字時間
        LocalDate date = LocalDate.now();
        LocalDateTime of = LocalDateTime.of(date, LocalTime.MIN);
        //設置此時間在芝加哥時區
        ZoneId cst = ZoneId.of("CST", ZoneId.SHORT_IDS);
        //芝加哥時間
        ZonedDateTime dateTime = of.atZone(cst);
        System.out.println(dateTime);
        //out出來是以芝加哥時間為準，此時我們是幾點
        Date out = Date.from(of.atZone(ZoneId.of("CST", ZoneId.SHORT_IDS)).toInstant());
        System.out.println(out);

        SeckillSessionEntity seckillSessionEntity = new SeckillSessionEntity();
        seckillSessionEntity.setId(7L);
//        Date from = Date.from(of.atZone(ZoneId.systemDefault()).toInstant());
//        seckillSessionEntity.setStartTime(from);
//        seckillSessionService.save(seckillSessionEntity);

        System.out.println(seckillSessionService.getById(seckillSessionEntity).getStartTime());

    }
    @Test
    public void service() {
        LocalDate now = LocalDate.now();
        LocalDate beginDay = now.plusDays(0);
        LocalDate endDay = now.plusDays(1);

        LocalDateTime min = LocalDateTime.of(beginDay, LocalTime.MIN);
        LocalDateTime max = LocalDateTime.of(endDay, LocalTime.MAX);
        String formatMin = min.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-ss-mm"));
        String formatMax = max.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-ss-mm"));
        QueryWrapper<SeckillSessionEntity> sessionWrapper = new QueryWrapper<>();
        sessionWrapper.ge("start_time",formatMin).and(item ->{
            item.le("end_time",formatMax);
        });
        List<SeckillSessionEntity> list = service.list(sessionWrapper);
        System.out.println(list);
    }
}