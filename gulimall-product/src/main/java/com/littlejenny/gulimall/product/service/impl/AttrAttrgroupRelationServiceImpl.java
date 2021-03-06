package com.littlejenny.gulimall.product.service.impl;

import com.littlejenny.common.constant.ProductConstants;
import com.littlejenny.gulimall.product.entity.AttrEntity;
import com.littlejenny.gulimall.product.entity.AttrGroupEntity;
import com.littlejenny.gulimall.product.service.AttrGroupService;
import com.littlejenny.gulimall.product.service.AttrService;
import com.littlejenny.gulimall.product.vo.AttrAttrgroupRelationEntityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.littlejenny.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.littlejenny.gulimall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrAttrgroupRelationDao dao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void deleteBatchRelation(AttrAttrgroupRelationEntityVO[] vos) {
        List<AttrAttrgroupRelationEntity> collect = Arrays.stream(vos).map((item) -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item,entity);
            return entity;
        }).collect(Collectors.toList());
        dao.deleteBatch(collect);
    }

    @Override
    public PageUtils listNoattrRelation(Long selfGroupId, Map<String, Object> params) {
        String key = (String) params.get("key");
        AttrGroupEntity selfgroup = attrGroupService.getById(selfGroupId);
        //1.??????????????????????????????catelogID???????????????
        QueryWrapper<AttrGroupEntity> gWrapper = new QueryWrapper<>();
        gWrapper.eq("catelog_id",selfgroup.getCatelogId());
        //??????????????????????????????
        List<AttrGroupEntity> groups = attrGroupService.list(gWrapper);
        //2.????????????????????????????????????????????????????????????
        QueryWrapper<AttrAttrgroupRelationEntity> aaWrapper = new QueryWrapper<>();
        //?????????????????????????????????
        //???????????????????????????????????????????????????????????????
        //???????????????????????????Controller???????????????????????????0
        //?????????????????????????????????
        QueryWrapper<AttrEntity> aWrapper = new QueryWrapper<>();
        if(groups != null && groups.size() > 0){
            List<Long> gIds = groups.stream().map((item) -> {
                return item.getAttrGroupId();
            }).collect(Collectors.toList());
            aaWrapper.in("attr_group_id",gIds);
            //????????????????????????????????????
            List<AttrAttrgroupRelationEntity> aaEntites = this.list(aaWrapper);
            List<Long> aIds = aaEntites.stream().map((item) -> {
                return item.getAttrId();
            }).collect(Collectors.toList());
            //4.???2?????????3
            if(aIds.size() > 0)aWrapper.notIn("attr_id",aIds);
        }
        //3.??????????????????catelogID???selfGroupID???????????????
        aWrapper.eq("catelog_id",selfgroup.getCatelogId());
        //5.??????????????????
        if(!"".equals(key)){
            aWrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        //6.??????????????????
        aWrapper.eq("attr_type", ProductConstants.AttrTypeEnum.ATTR_TYPE_BASE.getCode());
        // page: 1,//????????????
        // limit: 10,//???????????????
        IPage<AttrEntity> page = attrService.page(new Query<AttrEntity>().getPage(params), aWrapper);
        return new PageUtils(page);
    }

    @Override
    public void saveBatchByVOs(List<AttrAttrgroupRelationEntityVO> asList) {
        List<AttrAttrgroupRelationEntity> collect = asList.stream().map((item) -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, entity);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }
}