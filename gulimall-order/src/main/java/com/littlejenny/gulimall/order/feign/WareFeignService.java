package com.littlejenny.gulimall.order.feign;

import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.order.to.SubtarctStockTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Component
@FeignClient(value = "ware")
public interface WareFeignService {
    @RequestMapping("/ware/waresku/hasStockById")
    R hasStockByIds(@RequestBody List<Long> skuIds); //Map<Long, HasStockTO>
    @RequestMapping("/ware/waresku/subTractStock")
    R  subTractStock(@RequestBody List<SubtarctStockTO> subtarctStockTOS);//R.ok()
}
