package com.littlejenny.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.member.entity.MemberEntity;
import com.littlejenny.gulimall.member.exception.SameAccountException;
import com.littlejenny.gulimall.member.vo.regist.GoogleUserInfoVO;
import com.littlejenny.gulimall.member.vo.regist.LoginAccountVO;
import com.littlejenny.gulimall.member.vo.regist.RegistAccountVO;

import java.util.Map;

/**
 * 会员
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:32:06
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    MemberEntity regist(RegistAccountVO vo)throws SameAccountException;

    MemberEntity oauthLogin(GoogleUserInfoVO vo);

    MemberEntity login(LoginAccountVO vo);
}

