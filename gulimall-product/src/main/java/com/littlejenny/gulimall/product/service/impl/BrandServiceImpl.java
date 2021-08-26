package com.littlejenny.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.littlejenny.gulimall.product.entity.CategoryBrandRelationEntity;
import com.littlejenny.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.product.dao.BrandDao;
import com.littlejenny.gulimall.product.entity.BrandEntity;
import com.littlejenny.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<BrandEntity>();
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void updateDetailByID(BrandEntity brand) {
        this.updateById(brand);
        UpdateWrapper<CategoryBrandRelationEntity> set = new UpdateWrapper<CategoryBrandRelationEntity>()
                .eq("brand_id", brand.getBrandId())
                .set("brand_name", brand.getName());
        categoryBrandRelationService.update(set);
    }

    @Override
    public List<BrandEntity> getbyIds(List<Long> brandIds) {
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        wrapper.in("brand_id",brandIds);
        List<BrandEntity> list = this.list(wrapper);
        return list;
    }
}