package com.littlejenny.gulimall.product.service.impl;

import com.littlejenny.gulimall.product.entity.BrandEntity;
import com.littlejenny.gulimall.product.entity.CategoryEntity;
import com.littlejenny.gulimall.product.service.BrandService;
import com.littlejenny.gulimall.product.service.CategoryService;
import com.littlejenny.gulimall.product.vo.CategoryBrandRelationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.product.dao.CategoryBrandRelationDao;
import com.littlejenny.gulimall.product.entity.CategoryBrandRelationEntity;
import com.littlejenny.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public CategoryBrandRelationEntity[] catelogList(Long brandId) {
        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("brand_id",brandId);
        List<CategoryBrandRelationEntity> list = this.list(wrapper);
        return list.toArray(new CategoryBrandRelationEntity[list.size()]);
    }

    /**
     * ????????????????????????brandID???categoryID
     * @param categoryBrandRelation
     */
    @Transactional
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        this.save(fillDetail(categoryBrandRelation));
    }
    private CategoryBrandRelationEntity fillDetail(CategoryBrandRelationEntity categoryBrandRelation){
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        BrandEntity brand = brandService.getById(brandId);
        CategoryEntity category = categoryService.getById(catelogId);

        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(category.getName());
        return categoryBrandRelation;
    }
    /**
     * ????????????????????????brandID???categoryID
     * @param categoryBrandRelation
     */
    @Transactional
    @Override
    public void updateDetailById(CategoryBrandRelationEntity categoryBrandRelation) {
        //?????????fillDetail??????getName??????????????????????????????????????????????????????????????????????????????????????????
        //???????????????????????????????????????????????????????????????????????????
        this.updateById(fillDetail(categoryBrandRelation));
    }

    @Override
    public List<CategoryBrandRelationVO> getAllByCatId(Long catId) {
        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("catelog_id",catId);
        List<CategoryBrandRelationEntity> list = this.list(wrapper);
        List<CategoryBrandRelationVO> vos = list.stream().map(item -> {
            CategoryBrandRelationVO vo = new CategoryBrandRelationVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return vos;
    }
}