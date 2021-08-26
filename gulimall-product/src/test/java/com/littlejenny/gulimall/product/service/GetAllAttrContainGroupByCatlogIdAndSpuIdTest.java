package com.littlejenny.gulimall.product.service;

import com.littlejenny.gulimall.product.service.impl.AttrServiceImpl;
import com.littlejenny.gulimall.product.vo.item.SpuAttrGroupVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GetAllAttrContainGroupByCatlogIdAndSpuIdTest {
    @Autowired
    private AttrServiceImpl attrService;

    @Test
    public void getAllSaleAttrBySpuId() {
        List<SpuAttrGroupVO> list = attrService.getAllAttrContainGroupByCatlogIdAndSpuId(225L, 1L);
        System.out.println(list);
    }
}
