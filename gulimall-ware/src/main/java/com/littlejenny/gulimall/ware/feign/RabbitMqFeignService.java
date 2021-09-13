package com.littlejenny.gulimall.ware.feign;

import com.littlejenny.common.to.ware.WareOrderTaskDetailTO;
import com.littlejenny.common.to.ware.WareOrderTaskTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.littlejenny.gulimall.ware.entity.WareOrderTaskEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "rabbitmq")
public interface RabbitMqFeignService {
    @PostMapping("/taskDetail")
    R taskDetail(@RequestBody WareOrderTaskDetailEntity wareOrderTaskDetailEntity);//return R.ok();
    @PostMapping("/task")
    R task(@RequestBody WareOrderTaskEntity wareOrderTaskEntity);//return R.ok();

}
