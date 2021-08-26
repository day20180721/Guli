package com.littlejenny.gulimall.feign;
import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Service
@FeignClient(value = "product")
public interface FeignProductService {

    @RequestMapping("/product/category/info/{catId}")
    R catelogInfo(@PathVariable("catId") Long catId);

    @GetMapping("/product/brand/infos")
    R brandInfos(@RequestParam("brandIds") List<Long> brandIds);

    @RequestMapping("/product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") String attrId);
}