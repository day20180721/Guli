package com.littlejenny.gulimall.ware.feign;

import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("order")
public interface OrderFeignService {
    @RequestMapping("/order/order/info/sn/{sn}")
    R infoBySn(@PathVariable("sn") String sn);//OrderEntity
}
