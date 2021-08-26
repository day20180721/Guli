package com.littlejenny.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.to.HasStockTO;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.ware.entity.WareSkuEntity;
import com.littlejenny.common.exception.ware.NoWareCanHandleSkuException;
import com.littlejenny.gulimall.ware.to.SubtractStockTO;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:35:23
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageBySkuIdWareId(Map<String, Object> params);

    void updateStock(Long skuId, Long wareId, Integer skuNum);

    Map<Long, HasStockTO> hasStockByIds(List<Long> skuIds);

    void subTractStock(List<SubtractStockTO> subtarctStockTOS)throws NoWareCanHandleSkuException;
}

