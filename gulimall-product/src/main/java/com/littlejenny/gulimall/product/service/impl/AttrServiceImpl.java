package com.littlejenny.gulimall.product.service.impl;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.littlejenny.common.constant.Product;
import com.littlejenny.gulimall.product.entity.*;
import com.littlejenny.gulimall.product.service.*;
import com.littlejenny.gulimall.product.vo.AttrResponseVO;
import com.littlejenny.gulimall.product.vo.AttrVO;
import com.littlejenny.gulimall.product.vo.AttrValueVO;
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

import com.littlejenny.gulimall.product.dao.AttrDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * notice,only base operation need to find relation between attrgroup and attr
 */
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>();
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }
    @Transactional
    @Override
    public PageUtils queryPageByCatId(Map<String, Object> params, Long catId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>();
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("attr_id",key).or().like("attr_name",key);
        }

        if(catId != 0){
            wrapper.and((w)->{
                w.eq("catelog_id",catId);
            });
        }

        int intType = "base".equals(type) ? Product.AttrTypeEnum.ATTR_TYPE_BASE.getCode() : Product.AttrTypeEnum.ATTR_TYPE_SALE.getCode();
        wrapper.eq("attr_type",intType);

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVO> vos = records.stream().map((entity) -> {
            //我不太清楚為什麼要判斷null
            //因為一個屬性在創建時一定會指定一個分組
            //那在關係表內就一定有值
            //而catelog也一樣，創建時都必須指定的
            AttrResponseVO vo = new AttrResponseVO();
            BeanUtils.copyProperties(entity, vo);
            //目前有的資料有自己的attrID查出關係
            //因為我一開始判定只要是base都會有關連表內的屬性
            //但是我其實可以在分組內刪除關聯，導致這邊報null
            //分組頁面中刪除與屬性關係的時候應該一併把屬性裡面的分組ID也刪掉
            //所以總共會刪除關係與屬性
            if("base".equals(type)){
                QueryWrapper<AttrAttrgroupRelationEntity> aaWrapper = new QueryWrapper<>();
                aaWrapper.eq("attr_id", vo.getAttrId());
                AttrAttrgroupRelationEntity aaEntity = attrAttrgroupRelationService.getOne(aaWrapper);
                if(aaEntity != null){
                    //他這邊有判斷null
                    //用自己的attrID去關係表查自己屬於哪個組
                    // saving feature allows uploading form without groupID
                    // if you use the entity that have not init,
                    // that will give you a error
                    AttrGroupEntity gruopEntity = attrGroupService.getById(aaEntity.getAttrGroupId());
                    vo.setGroupName(gruopEntity.getAttrGroupName());
                }
            }
            //他這邊有判斷null
            //再用自己有的catelogID去查name
            CategoryEntity cateEntity = categoryService.getById(vo.getCatelogId());
            vo.setCatelogName(cateEntity.getName());
            return vo;
        }).collect(Collectors.toList());
        pageUtils.setList(vos);
        return pageUtils;
    }
    @Transactional
    @Override
    public void save(AttrVO attr) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr,entity);
        this.save(entity);
        //1=basic property
        if(entity.getAttrType() == Product.AttrTypeEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(entity.getAttrId());
            attrAttrgroupRelationService.save(relationEntity);
        }
    }
    @Transactional
    @Override
    public void removeDetailByIds(List<Long> asList) {
        if(asList.size() == 0)return;
        this.removeByIds(asList);
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
        int i = 0;
        wrapper.eq("attr_id",asList.get(0));
        i++;
        for(;i < asList.size();i++){
            wrapper.or().eq("attr_id",asList.get(i));
        }
        attrAttrgroupRelationService.remove(wrapper);
    }
    @Transactional
    @Override
    public AttrResponseVO getDetailById(Long attrId) {
        AttrEntity entity = this.getById(attrId);
        AttrResponseVO vo = new AttrResponseVO();
        BeanUtils.copyProperties(entity,vo);
        //回一個catelogpath

        //回顯分組，如果是BASE才需要
        if(vo.getAttrType() == Product.AttrTypeEnum.ATTR_TYPE_BASE.getCode()){
            QueryWrapper<AttrAttrgroupRelationEntity> aaWrapper = new QueryWrapper<>();
            aaWrapper.eq("attr_id", vo.getAttrId());

            //BASE未必會在關聯表有數據
            AttrAttrgroupRelationEntity aaEntity = attrAttrgroupRelationService.getOne(aaWrapper);
            if(aaEntity != null){
                //即便有關聯表內的數據，group裡面也未必有值
                if(aaEntity.getAttrGroupId() != null){
                    AttrGroupEntity groupEntity = attrGroupService.getById(aaEntity.getAttrGroupId());
                    vo.setGroupName(groupEntity.getAttrGroupName());
                    vo.setAttrGroupId(groupEntity.getAttrGroupId());
                }
            }
        }

        //回顯分類
        Long[] categoryPath = categoryService.getCategoryPath(vo.getCatelogId());
        vo.setCatelogPath(categoryPath);
        CategoryEntity cateEntity = categoryService.getById(vo.getCatelogId());
        vo.setCatelogName(cateEntity.getName());
        return vo;
    }
    @Transactional
    @Override
    public void updateDetailById(AttrResponseVO attr) {
        //將VO轉換成自己，並且Update
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr,entity);
        this.updateById(entity);
        if(entity.getAttrType() == Product.AttrTypeEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            //有可能屬性本來就沒分組
            QueryWrapper<AttrAttrgroupRelationEntity> countWrapper = new QueryWrapper<>();
            countWrapper.eq("attr_id",attr.getAttrId());
            int count = attrAttrgroupRelationService.count(countWrapper);
            //關係表Update
            UpdateWrapper<AttrAttrgroupRelationEntity> wrapper = new UpdateWrapper<>();
            wrapper.eq("attr_id",attr.getAttrId());
            wrapper.set("attr_group_id",attr.getAttrGroupId());
            if(count > 0){
                attrAttrgroupRelationService.update(wrapper);
            }else {
                //why can't we use update,because if the sheet
                //have no record but you still use update to
                //operate it,end up this operation is not effective
                AttrAttrgroupRelationEntity aaEntity = new AttrAttrgroupRelationEntity();
                aaEntity.setAttrId(attr.getAttrId());
                aaEntity.setAttrGroupId(attr.getAttrGroupId());
                attrAttrgroupRelationService.save(aaEntity);
            }
        }
    }
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Transactional
    @Override
    public void updateAllBySpuId(Long spuId, List<AttrValueVO> vos) {
        //找出attrValue表中SpuID為spuid的，並且全部刪除
        QueryWrapper<ProductAttrValueEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        productAttrValueService.remove(wrapper);
        //在全部添加新的vos進去
        List<ProductAttrValueEntity> productAttrValueEntities = vos.stream().map(item -> {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(item, entity);
            entity.setSpuId(spuId);
            return entity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntities);
    }
    @Override
    public List<ProductAttrValueEntity> getKVbySpuId(Long spuId) {
        QueryWrapper<ProductAttrValueEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.list(wrapper);
        return productAttrValueEntities;
    }

    @Override
    public List<Long> filterSearchable(List<Long> unFilterAttrsIds) {
        List<Long> collect = unFilterAttrsIds.stream().filter(item -> {
            AttrEntity entity = getById(item);
            return entity.getSearchType() == Product.AttrSearchAble.CAN.getCode();
        }).collect(Collectors.toList());
        return collect;
    }
}