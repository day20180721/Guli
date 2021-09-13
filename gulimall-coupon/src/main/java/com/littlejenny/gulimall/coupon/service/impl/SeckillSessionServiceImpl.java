package com.littlejenny.gulimall.coupon.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.to.product.SkuInfoTO;
import com.littlejenny.common.to.seckill.SeckillSessionAndSkuTO;
import com.littlejenny.common.to.seckill.SeckillSessionTO;
import com.littlejenny.common.to.seckill.SeckillSkuRelationTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.littlejenny.gulimall.coupon.feign.ProductFeignService;
import com.littlejenny.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.coupon.dao.SeckillSessionDao;
import com.littlejenny.gulimall.coupon.entity.SeckillSessionEntity;
import com.littlejenny.gulimall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public SeckillSessionAndSkuTO getThreeDaySessionFromToday() {
        return getSessionTOBeginEnd(0L,2L);
    }
    @Autowired
    private SeckillSkuRelationService skuRelationService;
    @Autowired
    private ProductFeignService productFeignService;
    /**
     *
     * @param begin 必須為0<=，小於0代表以前
     * @param end 必須為>=0，大於0為代表以後
     * @return
     */
    public SeckillSessionAndSkuTO getSessionTOBeginEnd(Long begin,Long end){
        if(begin > 0 || end < 0 )return null;
        String[] minMaxDays = getMinMaxDays(begin, end);
        QueryWrapper<SeckillSessionEntity> sessionWrapper = new QueryWrapper<>();
        sessionWrapper.ge("start_time",minMaxDays[0]).and(item ->{
            item.le("end_time",minMaxDays[1]);
        }).orderByAsc("start_time");

        List<SeckillSessionEntity> list = list(sessionWrapper);
        if(list != null && list.size() > 0){
            List<SeckillSessionTO> sessionTOS = list.stream().map(session -> {
                SeckillSessionTO sessionTO = new SeckillSessionTO();
                BeanUtils.copyProperties(session,sessionTO);
                List<SeckillSkuRelationEntity> skus = skuRelationService.getBySessionID(session.getId());
                if(skus != null && skus.size() > 0){
                    List<SeckillSkuRelationTO> skuTos = skus.stream().map(sku -> {
                        SeckillSkuRelationTO skuTO = new SeckillSkuRelationTO();
                        BeanUtils.copyProperties(sku, skuTO);
                        skuTO.setBeginTime(session.getStartTime().getTime());
                        skuTO.setEndTime(session.getEndTime().getTime());
                        skuTO.setToken(UUID.randomUUID().toString());
                        R r = productFeignService.getSkuInfoById(sku.getSkuId());
                        if(r.isSuccess()){
                            SkuInfoTO info = r.getValue("skuInfo",new TypeReference<SkuInfoTO>() {
                            });
                            BeanUtils.copyProperties(info, skuTO);
                        }
                        return skuTO;
                    }).collect(Collectors.toList());
                    sessionTO.setSkus(skuTos);
                }
                return sessionTO;
            }).collect(Collectors.toList());
            SeckillSessionAndSkuTO fullTO = new SeckillSessionAndSkuTO();
            fullTO.setTos(sessionTOS);
            return fullTO;
        }else {
            return null;
        }
    }
    private String[] getMinMaxDays(Long begin,Long end){
        LocalDate now = LocalDate.now();
        LocalDate beginDay = now.plusDays(begin);
        LocalDate endDay = now.plusDays(end);

        LocalDateTime min = LocalDateTime.of(beginDay, LocalTime.MIN);
        LocalDateTime max = LocalDateTime.of(endDay, LocalTime.MAX);
        String formatMin = min.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-ss-mm"));
        String formatMax = max.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-ss-mm"));
        String[] days = new String[]{formatMin,formatMax};
        return days;
    }
}