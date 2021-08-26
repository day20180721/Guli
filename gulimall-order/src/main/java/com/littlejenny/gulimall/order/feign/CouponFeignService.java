package com.littlejenny.gulimall.order.feign;

import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@FeignClient(value = "coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/spubounds/info/sku/{skuId}")
    R infoBySkuId(@PathVariable("skuId") Long skuId);//SpuBoundsEntity R.ok().put("spuBounds", spuBounds);

}
