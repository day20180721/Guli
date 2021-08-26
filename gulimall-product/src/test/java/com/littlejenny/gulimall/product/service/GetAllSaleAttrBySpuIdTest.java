package com.littlejenny.gulimall.product.service;
import com.littlejenny.gulimall.product.service.impl.AttrServiceImpl;
import com.littlejenny.gulimall.product.vo.item.SkuAttrGroupVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetAllSaleAttrBySpuIdTest {
    @Autowired
    private AttrServiceImpl attrService;

    @Test
    public void getAllSaleAttrBySpuId() {
        List<SkuAttrGroupVO> allSaleAttrBySpuId = attrService.getAllSaleAttrBySpuId(1L);
        System.out.println(allSaleAttrBySpuId.get(0));
    }
}
