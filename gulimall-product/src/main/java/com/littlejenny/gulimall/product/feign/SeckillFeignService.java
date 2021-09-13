package com.littlejenny.gulimall.product.feign;

import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.product.feignfallback.SeckillFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "seckill",fallback = SeckillFeignFallback.class)
public interface SeckillFeignService {
    @GetMapping("/isOnline/{skuId}")
    R isOnlineBySku(@PathVariable("skuId")Long skuId);//SeckillSkuRelationTO
}
