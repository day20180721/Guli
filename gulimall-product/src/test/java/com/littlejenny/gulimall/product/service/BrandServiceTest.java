package com.littlejenny.gulimall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.littlejenny.gulimall.product.entity.BrandEntity;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class BrandServiceTest {
    @Autowired
    BrandService service;
    @Test
    public void save() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("一條測試數據");
        brandEntity.setName("測試一號");
        service.save(brandEntity);
        System.out.println("save successfully");
    }
    @Test
    public void update() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setDescript("一條測試數據");
        brandEntity.setName("測試二號");
        service.updateById(brandEntity);
        System.out.println("update successfully");
    }
    @Test
    public void queryUseWrapper() {
        QueryWrapper<BrandEntity> brandEntityQueryWrapper = new QueryWrapper<>();
        QueryWrapper<BrandEntity> brand_id = brandEntityQueryWrapper.eq("brand_id", 1L);
        List<BrandEntity> list = service.list(brand_id);
        list.forEach((item) ->{
            System.out.println(item);
        });
        System.out.println("querypage successfully");
    }
}