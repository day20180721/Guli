package com.littlejenny.gulimall.ware.service;

import com.littlejenny.gulimall.ware.entity.WareInfoEntity;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class WareInfoServiceTest {
    @Autowired
    WareInfoService service;
    @Test
    void save() {
        WareInfoEntity infoEntity = new WareInfoEntity();
        infoEntity.setAddress("127.0.0.1");
        service.save(infoEntity);
    }
}