package com.littlejenny.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.littlejenny.gulimall.product.entity.CategoryBrandRelationEntity;
import com.littlejenny.gulimall.product.service.CategoryBrandRelationService;
import com.littlejenny.gulimall.product.vo.Catelog2VO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.product.dao.CategoryDao;
import com.littlejenny.gulimall.product.entity.CategoryEntity;
import com.littlejenny.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
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
        //1.檢查是否為別人的父菜單
        //邏輯刪除，只的是標示該物件為已刪除，不同於直接刪除
        baseMapper.deleteBatchIds(catIds);
    }

    @Override
    public Long[] getCategoryPath(Long attrGroupId) {
        List<Long> path = new ArrayList<>();
        _getCategoryPath(attrGroupId,path);
        return path.toArray(new Long[path.size()]);
    }


//    @Caching(evict = {
//            @CacheEvict(value = {"category"},key = "'getAllLevelOne'"),
//            @CacheEvict(value = {"category"},key = "'getCatalogJson'")
//    })
    //體現分區好處，如果菜單改變了，是必有關他的緩存都會不一致，此時將allEntries改為true代表刪除整個分區的內容
    @CacheEvict(value = {"category"},allEntries = true)
    @Transactional
    @Override
    public void updateDetailByID(CategoryEntity category) {
        this.updateById(category);
        UpdateWrapper<CategoryBrandRelationEntity> set = new UpdateWrapper<CategoryBrandRelationEntity>()
                .eq("catelog_id", category.getCatId())
                .set("catelog_name", category.getName());
        categoryBrandRelationService.update(set);

    }
    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public List<CategoryEntity> getAllLevelOne() {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("cat_level",1);
        List<CategoryEntity> levelOneList = baseMapper.selectList(wrapper);
        return levelOneList;
    }
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2VO>> getCatalogJson(){
        List<CategoryEntity> all = baseMapper.selectList(null);
        List<CategoryEntity> allLevelOne = getChildsByPid(all,0L);
        //找到一級分類
        Map<String, List<Catelog2VO>> map = allLevelOne.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();
        }, v -> {
            //找二級分類
            List<CategoryEntity> levelTwoList = getChildsByPid(all,v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if(levelTwoList != null && levelTwoList.size() > 0){
                //封裝二級分類VO
                catelog2VOS = levelTwoList.stream().map(item2 -> {
                    Catelog2VO vo2 = new Catelog2VO();
                    vo2.setCatalog1Id(item2.getParentCid().toString());
                    vo2.setId(item2.getCatId().toString());
                    vo2.setName(item2.getName());
                    //找三級分類
                    QueryWrapper<CategoryEntity> wrapperThree = new QueryWrapper<>();
                    wrapperThree.eq("parent_cid", item2.getCatId());
                    List<CategoryEntity> levelThreeList = getChildsByPid(all,item2.getCatId());
                    //封裝三級分類VO
                    List<Catelog2VO.Catelog3VO> catelog3VOS = null;
                    if(levelThreeList != null && levelThreeList.size() > 0){
                        catelog3VOS = levelThreeList.stream().map(item3 -> {
                            Catelog2VO.Catelog3VO vo3 = new Catelog2VO.Catelog3VO();
                            vo3.setCatalog2Id(item3.getParentCid().toString());
                            vo3.setName(item3.getName());
                            vo3.setId(item3.getCatId().toString());
                            return vo3;
                        }).collect(Collectors.toList());
                        vo2.setCatalog3List(catelog3VOS);
                    }
                    return vo2;
                }).collect(Collectors.toList());
            }
            return catelog2VOS;
        }));
        return map;
    }
    @Deprecated
    public Map<String, List<Catelog2VO>> getCatalogJson_old(){
        Map<String, List<Catelog2VO>> result = null;
        String getCatalogJsonString = redisTemplate.opsForValue().get("getCatalogJsonDetail");
        if(StringUtils.isEmpty(getCatalogJsonString)){
            RLock rLock = redissonClient.getLock("getCatalogJsonLock");
            rLock.lock(30L, TimeUnit.SECONDS);
            try {
                result = getCatalogJsonDetail_old();
            }finally {
                rLock.unlock();
            }
        }else {
            result = JSON.parseObject(getCatalogJsonString, new TypeReference<Map<String, List<Catelog2VO>>>() {});
        }
        return result;
    }
    @Deprecated
    public Map<String, List<Catelog2VO>> getCatalogJsonDetail_old() {
        String getCatalogJsonString = redisTemplate.opsForValue().get("getCatalogJsonDetail");
        if(!StringUtils.isEmpty(getCatalogJsonString)){
            Map<String, List<Catelog2VO>> getCatalogJsonMap = JSON.parseObject(getCatalogJsonString, new TypeReference<Map<String, List<Catelog2VO>>>() {
            });
            return getCatalogJsonMap;
        }
        List<CategoryEntity> all = baseMapper.selectList(null);
        List<CategoryEntity> allLevelOne = getChildsByPid(all,0L);
        //找到一級分類
        Map<String, List<Catelog2VO>> map = allLevelOne.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();
        }, v -> {
            //找二級分類
            List<CategoryEntity> levelTwoList = getChildsByPid(all,v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if(levelTwoList != null && levelTwoList.size() > 0){
                //封裝二級分類VO
                catelog2VOS = levelTwoList.stream().map(item2 -> {
                    Catelog2VO vo2 = new Catelog2VO();
                    vo2.setCatalog1Id(item2.getParentCid().toString());
                    vo2.setId(item2.getCatId().toString());
                    vo2.setName(item2.getName());
                    //找三級分類
                    QueryWrapper<CategoryEntity> wrapperThree = new QueryWrapper<>();
                    wrapperThree.eq("parent_cid", item2.getCatId());
                    List<CategoryEntity> levelThreeList = getChildsByPid(all,item2.getCatId());
                    //封裝三級分類VO
                    List<Catelog2VO.Catelog3VO> catelog3VOS = null;
                    if(levelThreeList != null && levelThreeList.size() > 0){
                        catelog3VOS = levelThreeList.stream().map(item3 -> {
                            Catelog2VO.Catelog3VO vo3 = new Catelog2VO.Catelog3VO();
                            vo3.setCatalog2Id(item3.getParentCid().toString());
                            vo3.setName(item3.getName());
                            vo3.setId(item3.getCatId().toString());
                            return vo3;
                        }).collect(Collectors.toList());
                        vo2.setCatalog3List(catelog3VOS);
                    }
                    return vo2;
                }).collect(Collectors.toList());
            }
            return catelog2VOS;
        }));
        String s = JSON.toJSONString(map);
        log.info("{} 進來分布鎖後透過資料庫拿到資料，且資料數為{}",Thread.currentThread().getName(),s.length());
        redisTemplate.opsForValue().set("getCatalogJsonDetail",s);
        return map;
    }
    private List<CategoryEntity> getChildsByPid(List<CategoryEntity> all, Long catId){
        //找到ParentID 為自己的人
        return all.stream().filter(item->item.getParentCid() == catId).collect(Collectors.toList());
    }
    private List<Long> _getCategoryPath(Long attrGroupId,List<Long> path){
        CategoryEntity byId = this.getById(attrGroupId);
        if(byId.getParentCid() != 0){
            _getCategoryPath(byId.getParentCid(),path);
        }
        path.add(attrGroupId);
        return path;
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