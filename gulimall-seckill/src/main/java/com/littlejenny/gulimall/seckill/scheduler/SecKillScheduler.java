package com.littlejenny.gulimall.seckill.scheduler;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.to.seckill.SeckillSessionAndSkuTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.seckill.constants.SecKillConstants;
import com.littlejenny.gulimall.seckill.feign.CouponFeignService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SecKillScheduler {
    /**
     * 找到所有場次
     * 場次Key為Begin_End，因為會直接用Key*找全部的場次，然後直接Split相減找會比較快
     * 找到場次對應的SKU
     * (SessionID,HashMap)->HashMap(Skuid,SecSkuinfo)
     * 設置對應SKU的信號量
     * (SessionID_SkuId,integer)
     */
    /**
     * 頁面商品項需要紀錄:場次號、物品號、識別碼
     * 尋找最近的活動需要開始及結束時間
     */
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private RedissonClient client;
    @Scheduled(cron = "*/30 * * * * ?")
    public void updateSKInfo(){
        RLock lock = client.getLock(SecKillConstants.UPDATEINFO_LOCK);
        try{
            lock.lock();
            R r =  couponFeignService.getThreeDaySessionFromToday();
            if(r.isSuccess()){
                SeckillSessionAndSkuTO to = r.getData(new TypeReference<SeckillSessionAndSkuTO>() {
                });
                if(to != null && to.getTos() != null && to.getTos().size() > 0){
                    to.getTos().stream().forEach(session ->{
                        //設定Key為start-end，方便以後查找近期開始的場次
                        long startTime = session.getStartTime().getTime();
                        long endTime = session.getEndTime().getTime();
                        String sessionKey = SecKillConstants.SESSION_PREFIX+ startTime+"-"+endTime;
                        if(!redis.hasKey(sessionKey)){
                            //將場次放到Redis
                            long liveTime = endTime - startTime;
                            System.out.println(liveTime);
                            redis.opsForValue().set(sessionKey,session.getId().toString(),liveTime, TimeUnit.MILLISECONDS);
                            //獲得某場次所有Sku的操作
                            BoundHashOperations<String, String, String> skuOps = redis.boundHashOps(SecKillConstants.SKUS_PREFIX + session.getId().toString());

                            if(session.getSkus() != null && session.getSkus().size() > 0){
                                Map<String,String> skuMap = new HashMap<>();
                                session.getSkus().stream().forEach(sku ->{
                                    //將搶購的SKU放到Redis
                                    String skuJson = JSON.toJSONString(sku);
                                    skuMap.put(sku.getSkuId().toString(),skuJson);
                                    //放信號量
                                    String semaphoreKey = SecKillConstants.SEMAPHORE_PREFIX + session.getId()+"-"+sku.getSkuId();
                                    Integer count = sku.getSeckillCount().intValue();
                                    redis.opsForValue().set(semaphoreKey,count.toString(),liveTime,TimeUnit.MILLISECONDS);
                                });
                                skuOps.putAll(skuMap);
                                redis.expire(SecKillConstants.SKUS_PREFIX + session.getId().toString(),liveTime,TimeUnit.MILLISECONDS);
                            }
                        }
                    });
                }
            }
        }finally {
            lock.unlock();
        }
    }
}
