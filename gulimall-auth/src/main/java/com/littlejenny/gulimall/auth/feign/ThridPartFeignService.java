package com.littlejenny.gulimall.auth.feign;

import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "thirdPart")
public interface ThridPartFeignService {
    @GetMapping("/thirdPart/sms/sendSms")
    R sendSms(@RequestParam("phone") String phone,@RequestParam("code") String code);

}
