package com.littlejenny.gulimall.product.dao;

import com.littlejenny.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.littlejenny.gulimall.product.vo.item.SkuAttrGroupVO;
import com.littlejenny.gulimall.product.vo.item.SpuAttrGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 15:11:53
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<SkuAttrGroupVO> getAllSaleAttrBySpuId(@Param("spuId") Long spuId);

    List<SpuAttrGroupVO> getAllAttrContainGroupByCatlogId(@Param("catalogId")Long catalogId, @Param("spuId")Long spuId);
}
