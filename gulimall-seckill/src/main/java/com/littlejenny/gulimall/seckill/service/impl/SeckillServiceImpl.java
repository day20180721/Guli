package com.littlejenny.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.littlejenny.common.constant.RabbitmqConstants;
import com.littlejenny.common.entity.VisitorLoginState;
import com.littlejenny.common.enums.order.OrderStatusEnum;
import com.littlejenny.common.to.order.OrderTO;
import com.littlejenny.common.to.seckill.SeckillSessionTO;
import com.littlejenny.common.to.seckill.SeckillSkuRelationTO;
import com.littlejenny.gulimall.seckill.constants.SecKillConstants;
import com.littlejenny.gulimall.seckill.feign.RabbitMqFeignService;
import com.littlejenny.gulimall.seckill.filter.UserFilter;
import com.littlejenny.gulimall.seckill.service.SeckillService;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private RedissonClient client;

    @Override
    public SeckillSessionTO getCurrentEvent() {
        List<String> keys = redis.keys(SecKillConstants.SESSION_PREFIX + "*").stream().collect(Collectors.toList());
        keys = keys.stream().sorted((item, item2) -> {
            String startTime1 = item.replace(SecKillConstants.SESSION_PREFIX, "").split("-")[0];
            long time1 = Long.parseLong(startTime1);
            String startTime2 = item2.replace(SecKillConstants.SESSION_PREFIX, "").split("-")[0];
            long time2 = Long.parseLong(startTime2);
            if (time1 > time2) return 1;
            else if (time1 == time2) return 0;
            else return -1;
        }).collect(Collectors.toList());

        for (String key : keys) {
            key = key.replace(SecKillConstants.SESSION_PREFIX, "");
            String[] split = key.split("-");
            Long begin = Long.parseLong(split[0]);
            Long end = Long.parseLong(split[1]);
            if (System.currentTimeMillis() > end) continue;
            SeckillSessionTO sessionTO = new SeckillSessionTO();
            String sessionID = redis.opsForValue().get(SecKillConstants.SESSION_PREFIX + key);
            sessionTO.setId(Long.parseLong(sessionID));
            BoundHashOperations<String, String, String> skuOps = redis.boundHashOps(SecKillConstants.SKUS_PREFIX + sessionID);
            List<String> skuJsons = skuOps.values();
            List<SeckillSkuRelationTO> skus = null;
            if (System.currentTimeMillis() > begin && System.currentTimeMillis() < end) {
                //期間內
                sessionTO.setOnline(true);
                skus = skuJsons.stream().map(item -> {
                    SeckillSkuRelationTO sku = JSON.parseObject(item, SeckillSkuRelationTO.class);
                    return sku;
                }).collect(Collectors.toList());

            } else {
                sessionTO.setOnline(false);
                skus = skuJsons.stream().map(item -> {
                    SeckillSkuRelationTO sku = JSON.parseObject(item, SeckillSkuRelationTO.class);
                    sku.setToken("");
                    return sku;
                }).collect(Collectors.toList());
            }
            sessionTO.setSkus(skus);
            return sessionTO;
        }
        return null;
    }

    @Override
    public SeckillSkuRelationTO isOnlineBySku(Long skuId) {
        SeckillSessionTO currentEvent = getCurrentEvent();
        if (currentEvent == null) return null;
        if (currentEvent.getOnline()) {
            for (SeckillSkuRelationTO sku : currentEvent.getSkus()) {
                if (sku.getSkuId() == skuId) {
                    return sku;
                }
            }
        }
        return null;
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public SeckillSkuRelationTO seckill(Long sessionId, Long skuId, String token, Integer count) {
        long begin = System.currentTimeMillis();
        SeckillSkuRelationTO onlineBySku = isOnlineBySku(skuId);
        if (onlineBySku != null && onlineBySku.getToken().equals(token) && onlineBySku.getSeckillLimit().intValue() >= count) {
            VisitorLoginState loginState = UserFilter.visitorLoginState.get();
            String buyRecordKey = SecKillConstants.BUYRECOED_PREFIX + sessionId + "-"+skuId;
            BoundHashOperations<String, String, String> ops = redis.boundHashOps(buyRecordKey);

            if(ops.hasKey(loginState.getUserId())){
                String buyed = ops.get(loginState.getUserId());
                int parseInt = Integer.parseInt(buyed);
                if(onlineBySku.getSeckillLimit().intValue() - parseInt < count){
                    return null;
                }
            }

            String key = SecKillConstants.SEMAPHORE_PREFIX + sessionId + "-" + skuId;
            RSemaphore semaphore = client.getSemaphore(key);
            boolean tryAcquire = semaphore.tryAcquire(count);
            if (tryAcquire) {


                Integer buyed = 0;
                if(ops.get(loginState.getUserId()) != null){
                    buyed = Integer.parseInt(ops.get(loginState.getUserId()));
                }
                Integer totalBuy =buyed + count;
                Long offset = onlineBySku.getEndTime() - System.currentTimeMillis();
                ops.put(loginState.getUserId(),totalBuy.toString());
                //增加過期時間
                redis.expire(buyRecordKey,offset,TimeUnit.MILLISECONDS);

                OrderTO order = new OrderTO();
                order.setCreateTime(new Date());
                order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
                order.setOrderSn(IdWorker.getTimeId());
                order.setTotalAmount(onlineBySku.getSeckillPrice());
                order.setMemberId(Long.parseLong(loginState.getUserId()));
                //rabbitmq
                try {
                    rabbitTemplate.convertAndSend(
                            RabbitmqConstants.HANDLEORDER_EXCHANGE,
                            RabbitmqConstants.HANDLESECORDER_QUEUE_KEY,
                            order);
                    long end = System.currentTimeMillis();
                    System.out.println(end - begin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return onlineBySku;
            }
        }
        return null;
    }
}
