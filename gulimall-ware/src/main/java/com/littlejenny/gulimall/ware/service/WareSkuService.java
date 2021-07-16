package com.littlejenny.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.ware.entity.WareSkuEntity;

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
}

