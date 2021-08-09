package com.littlejenny.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.littlejenny.gulimall.product.vo.AttrAttrgroupRelationEntityVO;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 15:11:53
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteBatchRelation(AttrAttrgroupRelationEntityVO[] vos);

    PageUtils listNoattrRelation(Long selfGroupId, Map<String, Object> params);

    void saveBatchByVOs(List<AttrAttrgroupRelationEntityVO> asList);
}

