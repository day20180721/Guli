package com.littlejenny.gulimall.order.feign;

import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@FeignClient(value = "product")
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/info/{skuId}") //SkuInfoEntity R.ok().put("skuInfo", skuInfo);
    R skuInfo(@PathVariable("skuId") Long skuId);

}
