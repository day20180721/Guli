package com.littlejenny.gulimall.seckill.feign;

import com.littlejenny.common.to.order.OrderTO;
import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "rabbitmq")
public interface RabbitMqFeignService {
    @PostMapping("/secOrder")
    R secOrder(@RequestBody OrderTO OrderTO);//return R.ok();

}
