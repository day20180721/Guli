package com.littlejenny.gulimall.order.dao;

import com.littlejenny.gulimall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:33:53
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
