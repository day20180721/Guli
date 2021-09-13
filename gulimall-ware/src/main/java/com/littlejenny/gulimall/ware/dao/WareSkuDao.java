package com.littlejenny.gulimall.ware.dao;

import com.littlejenny.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.littlejenny.gulimall.ware.to.HasStockWareTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:35:23
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void updateStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Integer hasStockById(@Param("skuId") Long skuId);

    List<HasStockWareTO> getHasStockWareBySKuID(@Param("skuId") Long skuId,@Param("quantity") Integer quantity);

    Integer lockStock(@Param("skuId") Long skuId,@Param("quantity") Integer quantity,@Param("wareId")Long wareId);

    Integer unlockStock(@Param("skuId") Long skuId,@Param("quantity") Integer quantity,@Param("wareId")Long wareId);

}
