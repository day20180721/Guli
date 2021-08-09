package com.littlejenny.gulimall.controller;

import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.common.to.es.SkuEsModel;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("elastic/sku")
public class SkuController {
    @Autowired
    private SkuService skuService;
    @PostMapping("/save")
    public R save(@RequestBody List<SkuEsModel> models){
        try {
            boolean b = skuService.saveBatch(models);
            if(b){
                return R.ok();
            }else {
                return R.error(BizCodeEnum.ESUPSKU_EXCEPTION.getCode(),BizCodeEnum.ESUPSKU_EXCEPTION.getMessage());
            }
        } catch (IOException e) {
            log.error("連接異常{}",e);
            return R.error(BizCodeEnum.ELASTICIO_EXCEPTION.getCode(),BizCodeEnum.ELASTICIO_EXCEPTION.getMessage());
        }
    }
}
