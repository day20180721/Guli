package com.littlejenny.gulimall.ware.service.impl;

import com.littlejenny.gulimall.ware.feign.RabbitMqFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.ware.dao.WareOrderTaskDao;
import com.littlejenny.gulimall.ware.entity.WareOrderTaskEntity;
import com.littlejenny.gulimall.ware.service.WareOrderTaskService;


@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {
    @Autowired
    private RabbitMqFeignService rabbit;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskEntity> page = this.page(
                new Query<WareOrderTaskEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(WareOrderTaskEntity wareOrderTask) {
        this.save(wareOrderTask);
        //調用RabbitMQ傳送創建訂單訊息
        try{
            rabbit.task(wareOrderTask);
        }catch (Exception e){
            //網路錯誤，重新傳輸或是保存到SQL供後續檢查機制補償
        }
    }
}