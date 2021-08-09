package com.littlejenny.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.product.entity.SpuInfoEntity;
import com.littlejenny.gulimall.product.vo.addproduct.SpuVO;

import java.util.Map;

/**
 * spu信息
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 15:11:54
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(SpuVO spuvo);

    PageUtils queryByCidBidKeyStatus(Map<String, Object> params);

    void upSpuById(Long spuId);

}

