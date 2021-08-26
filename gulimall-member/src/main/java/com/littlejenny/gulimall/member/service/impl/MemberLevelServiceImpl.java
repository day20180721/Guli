package com.littlejenny.gulimall.member.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.member.dao.MemberLevelDao;
import com.littlejenny.gulimall.member.entity.MemberLevelEntity;
import com.littlejenny.gulimall.member.service.MemberLevelService;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Long getDefaultLevel() {
        QueryWrapper<MemberLevelEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("default_status",1);
        List<MemberLevelEntity> list = list(wrapper);
        if(list.size() == 1){
            return list.get(0).getId();
        }
        return null;
    }
}