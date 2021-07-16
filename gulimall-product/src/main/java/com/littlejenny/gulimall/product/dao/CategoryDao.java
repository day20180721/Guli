package com.littlejenny.gulimall.product.dao;

import com.littlejenny.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 15:11:54
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
