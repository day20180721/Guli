package com.littlejenny.gulimall.product.service.impl;

import com.littlejenny.common.constant.Product;
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
        //1.在分組表內找到與自己catelogID相同的分組
        QueryWrapper<AttrGroupEntity> gWrapper = new QueryWrapper<>();
        gWrapper.eq("catelog_id",selfgroup.getCatelogId());
        //取得該分類下所有分組
        List<AttrGroupEntity> groups = attrGroupService.list(gWrapper);
        //2.用這些分組去分組與屬性表中查找所有的屬性
        QueryWrapper<AttrAttrgroupRelationEntity> aaWrapper = new QueryWrapper<>();
        //如果該分類下有分組在查
        //這邊正常來說最少會一個分組，就是查詢者本身
        //如果用其他方式訪問Controller就有可能出現結果為0
        //此時連過濾都不用過濾了
        QueryWrapper<AttrEntity> aWrapper = new QueryWrapper<>();
        if(groups != null && groups.size() > 0){
            List<Long> gIds = groups.stream().map((item) -> {
                return item.getAttrGroupId();
            }).collect(Collectors.toList());
            aaWrapper.in("attr_group_id",gIds);
            //取得所有分組所對應的屬性
            List<AttrAttrgroupRelationEntity> aaEntites = this.list(aaWrapper);
            List<Long> aIds = aaEntites.stream().map((item) -> {
                return item.getAttrId();
            }).collect(Collectors.toList());
            //4.用2排除掉3
            if(aIds.size() > 0)aWrapper.notIn("attr_id",aIds);
        }
        //3.查找屬性表內catelogID與selfGroupID相同的屬性
        aWrapper.eq("catelog_id",selfgroup.getCatelogId());
        //5.過濾查詢字斷
        if(!"".equals(key)){
            aWrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        //6.只要基本屬性
        aWrapper.eq("attr_type", Product.AttrTypeEnum.ATTR_TYPE_BASE.getCode());
        // page: 1,//当前页码
        // limit: 10,//每页记录数
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