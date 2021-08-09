package com.littlejenny.gulimall.product.feign;

import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Component
@FeignClient(value = "ware")
public interface WareService {
    @RequestMapping("/ware/waresku/hasStockById")
    R hasStockByIds(@RequestBody List<Long> skuIds);
}
