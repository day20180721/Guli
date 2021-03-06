package com.littlejenny.gulimall.product.service.impl;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.littlejenny.common.constant.ProductConstants;
import com.littlejenny.gulimall.product.entity.*;
import com.littlejenny.gulimall.product.service.*;
import com.littlejenny.gulimall.product.vo.AttrResponseVO;
import com.littlejenny.gulimall.product.vo.AttrVO;
import com.littlejenny.gulimall.product.vo.AttrValueVO;
import com.littlejenny.gulimall.product.vo.item.SkuAttrGroupVO;
import com.littlejenny.gulimall.product.vo.item.SpuAttrGroupVO;
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

        int intType = "base".equals(type) ? ProductConstants.AttrTypeEnum.ATTR_TYPE_BASE.getCode() : ProductConstants.AttrTypeEnum.ATTR_TYPE_SALE.getCode();
        wrapper.eq("attr_type",intType);

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVO> vos = records.stream().map((entity) -> {
            //?????????????????????????????????null
            //?????????????????????????????????????????????????????????
            //?????????????????????????????????
            //???catelog???????????????????????????????????????
            AttrResponseVO vo = new AttrResponseVO();
            BeanUtils.copyProperties(entity, vo);
            //??????????????????????????????attrID????????????
            //?????????????????????????????????base??????????????????????????????
            //???????????????????????????????????????????????????????????????null
            //?????????????????????????????????????????????????????????????????????????????????ID?????????
            //????????????????????????????????????
            if("base".equals(type)){
                QueryWrapper<AttrAttrgroupRelationEntity> aaWrapper = new QueryWrapper<>();
                aaWrapper.eq("attr_id", vo.getAttrId());
                AttrAttrgroupRelationEntity aaEntity = attrAttrgroupRelationService.getOne(aaWrapper);
                if(aaEntity != null){
                    //??????????????????null
                    //????????????attrID????????????????????????????????????
                    // saving feature allows uploading form without groupID
                    // if you use the entity that have not init,
                    // that will give you a error
                    AttrGroupEntity gruopEntity = attrGroupService.getById(aaEntity.getAttrGroupId());
                    vo.setGroupName(gruopEntity.getAttrGroupName());
                }
            }
            //??????????????????null
            //??????????????????catelogID??????name
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
        if(entity.getAttrType() == ProductConstants.AttrTypeEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null){
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
        //?????????catelogpath

        //????????????????????????BASE?????????
        if(vo.getAttrType() == ProductConstants.AttrTypeEnum.ATTR_TYPE_BASE.getCode()){
            QueryWrapper<AttrAttrgroupRelationEntity> aaWrapper = new QueryWrapper<>();
            aaWrapper.eq("attr_id", vo.getAttrId());

            //BASE??????????????????????????????
            AttrAttrgroupRelationEntity aaEntity = attrAttrgroupRelationService.getOne(aaWrapper);
            if(aaEntity != null){
                //?????????????????????????????????group?????????????????????
                if(aaEntity.getAttrGroupId() != null){
                    AttrGroupEntity groupEntity = attrGroupService.getById(aaEntity.getAttrGroupId());
                    vo.setGroupName(groupEntity.getAttrGroupName());
                    vo.setAttrGroupId(groupEntity.getAttrGroupId());
                }
            }
        }

        //????????????
        Long[] categoryPath = categoryService.getCategoryPath(vo.getCatelogId());
        vo.setCatelogPath(categoryPath);
        CategoryEntity cateEntity = categoryService.getById(vo.getCatelogId());
        vo.setCatelogName(cateEntity.getName());
        return vo;
    }
    @Transactional
    @Override
    public void updateDetailById(AttrResponseVO attr) {
        //???VO????????????????????????Update
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr,entity);
        this.updateById(entity);
        if(entity.getAttrType() == ProductConstants.AttrTypeEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            //?????????????????????????????????
            QueryWrapper<AttrAttrgroupRelationEntity> countWrapper = new QueryWrapper<>();
            countWrapper.eq("attr_id",attr.getAttrId());
            int count = attrAttrgroupRelationService.count(countWrapper);
            //?????????Update
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

    /**
     * @param spuId ????????????Attr???SpuId
     * @param vos ???SpuId???????????????Attr???
     */
    @Transactional
    @Override
    public void updateAllBySpuId(Long spuId, List<AttrValueVO> vos) {
        //??????attrValue??????SpuID???spuid????????????????????????
        QueryWrapper<ProductAttrValueEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        productAttrValueService.remove(wrapper);
        //?????????????????????vos??????
        List<ProductAttrValueEntity> productAttrValueEntities = vos.stream().map(item -> {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(item, entity);
            entity.setSpuId(spuId);
            return entity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntities);
    }

    /**
     * @param spuId ????????????????????????????????????????????????
     * @return
     */
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
            return entity.getSearchType() == ProductConstants.AttrSearchAble.CAN.getCode();
        }).collect(Collectors.toList());
        return collect;
    }
    @Override
    public List<SkuAttrGroupVO> getAllSaleAttrBySpuId(Long spuId) {
        List<SkuAttrGroupVO> list = this.baseMapper.getAllSaleAttrBySpuId(spuId);
        return list;
    }

    @Override
    public List<SpuAttrGroupVO> getAllAttrContainGroupByCatlogIdAndSpuId(Long catalogId, Long spuId) {
        List<SpuAttrGroupVO> list = this.baseMapper.getAllAttrContainGroupByCatlogId(catalogId,spuId);
        return list;
    }
}