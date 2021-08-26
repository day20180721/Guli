package com.littlejenny.gulimall.product.service.impl;

import com.littlejenny.gulimall.product.entity.SpuInfoDescEntity;
import com.littlejenny.gulimall.product.entity.SpuInfoEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.product.dao.SkuInfoDao;
import com.littlejenny.gulimall.product.entity.SkuInfoEntity;
import com.littlejenny.gulimall.product.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * key: 123
     * catelogId: 225
     * brandId: 20
     * min: 0
     * max: 5000
     */
    @Override
    public PageUtils queryByCidBidKeyMoney(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        String min = (String)params.get("min");
        if(min != null && min.matches("^\\d*$")){
            wrapper.ge("price",min);
        }
        String max = (String)params.get("max");
        if(max != null && max.matches("^[1-9]\\d*$")){
            wrapper.le("price",max);
        }
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w ->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        String brandId = (String)params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && brandId.matches("^[1-9]\\d*$")){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String)params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && catelogId.matches("^[1-9]\\d*$")){
            wrapper.eq("catalog_id",catelogId);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }
}