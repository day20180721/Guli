package com.littlejenny.gulimall.ware.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.ware.dao.PurchaseDetailDao;
import com.littlejenny.gulimall.ware.entity.PurchaseDetailEntity;
import com.littlejenny.gulimall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * key: 123
     * status: 0
     * wareId: 2
     */
    @Override
    public PageUtils queryPageByKeyStatusWareId(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key) && key.matches("^\\d*$")){
            wrapper.and(w -> {
                w.eq("id",key).or().eq("purchase_id",key).or().eq("sku_id",key);
            });
        }
        String status = (String)params.get("status");
        if(!StringUtils.isEmpty(status) && status.matches("^[0-4]$")){
            wrapper.eq("status",status);
        }
        String wareId = (String)params.get("wareId");
        if(!StringUtils.isEmpty(wareId) && wareId.matches("^\\d*$")){
            wrapper.eq("ware_id",wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }
}