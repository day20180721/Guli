package com.littlejenny.gulimall.coupon.dao;

import com.littlejenny.gulimall.coupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:26:22
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
