package com.littlejenny.gulimall.cart.feign;

import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@FeignClient(value = "product")
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/info/{skuId}") //SkuInfoEntity R.ok().put("skuInfo", skuInfo);
    R skuInfo(@PathVariable("skuId") Long skuId);
    @GetMapping("/product/skusaleattrvalue/info/id/{skuId}")
    List<String> saleAttrsBySkuId(@PathVariable("skuId") Long skuId);
}