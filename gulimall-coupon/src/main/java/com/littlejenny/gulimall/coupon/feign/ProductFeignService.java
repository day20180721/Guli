package com.littlejenny.gulimall.coupon.feign;

import com.littlejenny.common.to.product.SkuInfoTO;
import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@FeignClient(value = "product")
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfoById(@PathVariable("skuId") Long skuId);// return R.ok().put("skuInfo", skuInfo);

}
