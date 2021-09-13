package com.littlejenny.gulimall.seckill.service;

import com.littlejenny.common.to.seckill.SeckillSessionTO;
import com.littlejenny.common.to.seckill.SeckillSkuRelationTO;

public interface SeckillService {
    SeckillSessionTO getCurrentEvent();

    SeckillSkuRelationTO isOnlineBySku(Long skuId);

    SeckillSkuRelationTO seckill(Long sessionId, Long skuId, String token, Integer count);
}
