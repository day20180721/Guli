package com.littlejenny.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:32:06
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

