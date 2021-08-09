package com.littlejenny.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.product.entity.CategoryBrandRelationEntity;
import com.littlejenny.gulimall.product.vo.CategoryBrandRelationVO;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 15:11:54
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    CategoryBrandRelationEntity[] catelogList(Long brandId);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateDetailById(CategoryBrandRelationEntity categoryBrandRelation);

    List<CategoryBrandRelationVO> getAllByCatId(Long catId);
}

