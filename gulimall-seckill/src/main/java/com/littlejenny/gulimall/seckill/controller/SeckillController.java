package com.littlejenny.gulimall.seckill.controller;

import com.littlejenny.common.to.seckill.SeckillSessionTO;
import com.littlejenny.common.to.seckill.SeckillSkuRelationTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
public class SeckillController {
    @Autowired
    private SeckillService seckillService;
    @ResponseBody
    @GetMapping("/currentEvent")
    public R getCurrentEvent(){
        SeckillSessionTO event = seckillService.getCurrentEvent();
        return R.ok().setData(event);
    }
    @ResponseBody
    @GetMapping("/isOnline/{skuId}")
    public R isOnlineBySku(@PathVariable("skuId")Long skuId){
        SeckillSkuRelationTO onlineSku = seckillService.isOnlineBySku(skuId);
        return R.ok().setData(onlineSku);
    }
    @ResponseBody
    @GetMapping("/seckill")
    public R seckill(@RequestParam("sessionId") Long sessionId, @RequestParam("skuId")Long skuId,@RequestParam("token") String token,@RequestParam("count") Integer count){
        SeckillSkuRelationTO to = seckillService.seckill(sessionId,skuId,token,count);
        System.out.println(to);
        return R.ok().setData(to);
    }
}
