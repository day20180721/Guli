package com.littlejenny.gulimall.product.feignfallback;

import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.product.feign.SeckillFeignService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
public class SeckillFeignFallback implements SeckillFeignService {

    @Override
    public R isOnlineBySku(Long skuId) {
        return R.error(BizCodeEnum.REMOTESERVICE_EXCEPTION.getCode(), BizCodeEnum.REMOTESERVICE_EXCEPTION.getMessage());
    }
}
