package com.littlejenny.gulimall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.product.dao.CategoryDao;
import com.littlejenny.gulimall.product.entity.CategoryEntity;
import com.littlejenny.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
//以filter查找cid==0的元素，並將設置該元素的子例在將這個鏈表以元素的Sort值進行排序最後將他打收集成List
// sort 0為最高，
        List<CategoryEntity> level1 = categoryEntities.stream(
        ).filter((entity) -> entity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu,categoryEntities));
            return menu;
        }
        ).sorted((menu1,menu2)->{
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0: menu2.getSort());
        }).collect(Collectors.toList());

        return level1;
    }

    @Override
    public void removeMenuByIds(List<Long> catIds) {
        //TODO 1.檢查是否為別人的父菜單
        //邏輯刪除，只的是標示該物件為已刪除，不同於直接刪除
        baseMapper.deleteBatchIds(catIds);
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter((entity) -> {
            return entity.getParentCid() == root.getCatId();
        }).map((entity) -> {
            entity.setChildren(getChildrens(entity, all));
            return entity;
        }).sorted((menu1,menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0: menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }
}