package com.littlejenny.gulimall.ware.dao;

import com.littlejenny.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:35:23
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
