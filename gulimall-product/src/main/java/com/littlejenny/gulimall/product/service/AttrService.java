package com.littlejenny.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.product.entity.AttrEntity;
import com.littlejenny.gulimall.product.entity.ProductAttrValueEntity;
import com.littlejenny.gulimall.product.vo.AttrResponseVO;
import com.littlejenny.gulimall.product.vo.AttrVO;
import com.littlejenny.gulimall.product.vo.AttrValueVO;
import com.littlejenny.gulimall.product.vo.item.SkuAttrGroupVO;
import com.littlejenny.gulimall.product.vo.item.SpuAttrGroupVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 15:11:53
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPageByCatId(Map<String, Object> params, Long catId, String type);

    void save(AttrVO attr);

    void removeDetailByIds(List<Long> asList);

    AttrResponseVO getDetailById(Long attrId);

    void updateDetailById(AttrResponseVO attr);

    void updateAllBySpuId(Long spuId, List<AttrValueVO> vos);

    List<ProductAttrValueEntity> getKVbySpuId(Long spuId);

    List<Long> filterSearchable(List<Long> unFilterAttrsIds);

    List<SkuAttrGroupVO> getAllSaleAttrBySpuId(Long spuId);

    List<SpuAttrGroupVO> getAllAttrContainGroupByCatlogIdAndSpuId(Long catalogId, Long spuId);
}

