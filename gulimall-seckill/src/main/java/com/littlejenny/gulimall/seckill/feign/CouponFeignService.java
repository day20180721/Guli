package com.littlejenny.gulimall.seckill.feign;

import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient(value = "coupon")
public interface CouponFeignService {
    @GetMapping("/coupon/seckillsession/threedaysessions")
    R getThreeDaySessionFromToday();
}
