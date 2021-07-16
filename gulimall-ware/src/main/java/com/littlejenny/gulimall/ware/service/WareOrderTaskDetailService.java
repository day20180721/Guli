package com.littlejenny.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.ware.entity.WareOrderTaskDetailEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:35:23
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

