package com.littlejenny.gulimall.product.feign;


import com.littlejenny.common.to.es.SkuEsModel;
import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(value = "search")
public interface ElasticService {
    @PostMapping("/elastic/sku/save")
    R save(@RequestBody List<SkuEsModel> models);
}
