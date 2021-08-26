package com.littlejenny.gulimall.member.service.impl;

import com.littlejenny.gulimall.member.dao.MemberLevelDao;
import com.littlejenny.gulimall.member.exception.SameAccountException;
import com.littlejenny.gulimall.member.service.MemberLevelService;
import com.littlejenny.gulimall.member.vo.regist.GoogleUserInfoVO;
import com.littlejenny.gulimall.member.vo.regist.LoginAccountVO;
import com.littlejenny.gulimall.member.vo.regist.RegistAccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.member.dao.MemberDao;
import com.littlejenny.gulimall.member.entity.MemberEntity;
import com.littlejenny.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }
    @Autowired
    private MemberLevelService memberLevelService;
    @Override
    public MemberEntity regist(RegistAccountVO vo)throws SameAccountException {
        Integer exist = this.baseMapper.accountIsExist(vo.getUsername());
        if(exist > 0)throw new SameAccountException();
        MemberEntity entity = new MemberEntity();
        Long level = memberLevelService.getDefaultLevel();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        entity.setUsername(vo.getUsername());
        if(level != null)entity.setLevelId(level);
        entity.setMobile(vo.getPhone());
        entity.setPassword(encoder.encode(vo.getPassword()));
        entity.setCreateTime(new Date());
        save(entity);
        return entity;
    }

    @Override
    public MemberEntity oauthLogin(GoogleUserInfoVO vo) {
        String uid = vo.getId();
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid);
        MemberEntity memberEntity = this.getOne(wrapper);
        if(memberEntity != null){
            //TODO 更新登入時間
            //可以記錄在表ums_member_login_log
        }else {
            //新增用戶
            memberEntity = new MemberEntity();
            memberEntity.setUid(uid);
            memberEntity.setUsername(vo.getName());
            memberEntity.setCity(vo.getLocale());
            memberEntity.setCreateTime(new Date());
            Long level = memberLevelService.getDefaultLevel();
            if(level != null)memberEntity.setLevelId(level);
            save(memberEntity);
        }
        return memberEntity;
    }

    @Override
    public MemberEntity login(LoginAccountVO vo) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username",vo.getUsername()).or().eq("mobile",vo.getUsername());
        MemberEntity entity = this.baseMapper.selectOne(wrapper);
        if(entity == null){
            return null;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(vo.getPassword(), entity.getPassword());
        if(matches){
            return entity;
        }else{
            return null;
        }
    }
}