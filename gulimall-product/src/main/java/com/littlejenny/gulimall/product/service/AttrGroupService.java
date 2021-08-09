package com.littlejenny.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.product.entity.AttrEntity;
import com.littlejenny.gulimall.product.entity.AttrGroupEntity;
import com.littlejenny.gulimall.product.vo.AttrGroupEntityVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 15:11:53
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catId);

    List<AttrEntity> getAttrsByGroupId(Long groupId);

    void removeDetailByIds(List<Long> asList);

    List<AttrGroupEntityVO> getGroupwithattrByCatId(Long catId);
}

