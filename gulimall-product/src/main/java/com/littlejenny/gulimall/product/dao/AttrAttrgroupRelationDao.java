package com.littlejenny.gulimall.product.dao;

import com.littlejenny.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 15:11:53
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatch(@Param("entites") List<AttrAttrgroupRelationEntity> entites);
}
