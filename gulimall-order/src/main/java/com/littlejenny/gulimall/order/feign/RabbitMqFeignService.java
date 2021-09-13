package com.littlejenny.gulimall.order.feign;

import com.littlejenny.common.to.ware.WareOrderTaskTO;
import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "rabbitmq")
public interface RabbitMqFeignService {
    @PostMapping("/taskExpire")
    R taskExpire(@RequestBody WareOrderTaskTO wareOrderTaskTO);//return R.ok();
}
