package com.littlejenny.gulimall.order.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.order.dao.PaymentInfoDao;
import com.littlejenny.gulimall.order.entity.PaymentInfoEntity;
import com.littlejenny.gulimall.order.service.PaymentInfoService;


@Service("paymentInfoService")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoDao, PaymentInfoEntity> implements PaymentInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PaymentInfoEntity> page = this.page(
                new Query<PaymentInfoEntity>().getPage(params),
                new QueryWrapper<PaymentInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PaymentInfoEntity getByPayID(String paymentId) {
        QueryWrapper<PaymentInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("payment_id",paymentId);
        PaymentInfoEntity entity = this.baseMapper.selectOne(wrapper);
        return entity;
    }

}