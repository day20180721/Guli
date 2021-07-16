package com.littlejenny.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:33:53
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

