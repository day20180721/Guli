package com.littlejenny.gulimall.product.service.impl;

import com.littlejenny.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.littlejenny.gulimall.product.entity.AttrEntity;
import com.littlejenny.gulimall.product.service.AttrAttrgroupRelationService;
import com.littlejenny.gulimall.product.service.AttrService;
import com.littlejenny.gulimall.product.vo.AttrGroupEntityVO;
import com.littlejenny.gulimall.product.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.product.dao.AttrGroupDao;
import com.littlejenny.gulimall.product.entity.AttrGroupEntity;
import com.littlejenny.gulimall.product.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId) {
        String key = (String) params.get("key");
        //用catId判斷的用意是如果等於0，就等於把所有屬性全部列出
        if(catId == 0){
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
            if(!StringUtils.isEmpty(key)){
                //寫裡面跟寫外面有差 and ( id == 1 or name == "")
                wrapper.and((obj)->{
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }else{

            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catId);
            if(!StringUtils.isEmpty(key)){
                //寫裡面跟寫外面有差 and ( id == 1 or name == "")
                wrapper.and((obj)->{
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }
    }
    @Transactional
    @Override
    public List<AttrEntity> getAttrsByGroupId(Long groupId) {
        //從關係表找所有attrID
        QueryWrapper<AttrAttrgroupRelationEntity> aaWrapper = new QueryWrapper<>();
        aaWrapper.eq("attr_group_id",groupId);
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationService.list(aaWrapper);
        List<Long> attrIds = list.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //用attrIds找全部的entity
        Collection<AttrEntity> attrEntities = attrService.listByIds(attrIds);
        return (List<AttrEntity>)attrEntities;
    }

    @Transactional
    @Override
    public void removeDetailByIds(List<Long> groupIds) {
        this.removeByIds(groupIds);
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.in("attr_group_id",groupIds);
        attrAttrgroupRelationService.remove(wrapper);
    }
    @Transactional
    @Override
    public List<AttrGroupEntityVO> getGroupwithattrByCatId(Long catId) {
        //1.取得所有Group
        QueryWrapper<AttrGroupEntity> groupsWrapper = new QueryWrapper<>();
        groupsWrapper.eq("catelog_id",catId);
        List<AttrGroupEntity> groups = list(groupsWrapper);
        //2.每個Group封裝成Group+GroupAttr
        if(groups != null && groups.size() > 0){
            List<AttrGroupEntityVO> attr_groupvos = groups.stream().map(item -> {
                AttrGroupEntityVO groupvo = new AttrGroupEntityVO();
                BeanUtils.copyProperties(item, groupvo);
                //取得當前groupID
                Long attrGroupId = groupvo.getAttrGroupId();
                //去關聯表查詢該ID所對應的Attr
                QueryWrapper<AttrAttrgroupRelationEntity> aaWrapper = new QueryWrapper<>();
                aaWrapper.eq("attr_group_id", attrGroupId);
                List<AttrAttrgroupRelationEntity> aaEntites = attrAttrgroupRelationService.list(aaWrapper);
                //在用關聯表所關聯到的attr數據去attr表查詳細資訊
                if (aaEntites != null && aaEntites.size() != 0) {
                    List<AttrVO> attrVOs = aaEntites.stream().map(attrItem -> {
                        AttrVO attrVO = new AttrVO();
                        Long attrId = attrItem.getAttrId();
                        //詳細實體
                        AttrEntity attr = attrService.getById(attrId);
                        //封裝成實體VO
                        BeanUtils.copyProperties(attr, attrVO);
                        return attrVO;
                    }).collect(Collectors.toList());
                    groupvo.setAttrs(attrVOs);
                }
                return groupvo;
            }).collect(Collectors.toList());
            return attr_groupvos;
        }else{
            return null;
        }
    }
}