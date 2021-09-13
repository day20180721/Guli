package com.littlejenny.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.gulimall.member.exception.SameAccountException;
import com.littlejenny.gulimall.member.vo.regist.GoogleUserInfoVO;
import com.littlejenny.gulimall.member.vo.regist.LoginAccountVO;
import com.littlejenny.gulimall.member.vo.regist.RegistAccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.littlejenny.gulimall.member.entity.MemberEntity;
import com.littlejenny.gulimall.member.service.MemberService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.R;



/**
 * 会员
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:32:06
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @PostMapping("/regist")
    public R regist(@RequestBody RegistAccountVO vo){
        //判斷註冊是否成功，可能帳號會一樣
        try {
            MemberEntity entity = memberService.regist(vo);
            return R.ok().setData(entity);
        }catch (SameAccountException e){
            return R.error(BizCodeEnum.SAMEACCOUNT_EXCEPTION.getCode(), BizCodeEnum.SAMEACCOUNT_EXCEPTION.getMessage());
        }
    }
    @PostMapping("/oauthLogin")
    public R oauthLogin(@RequestBody GoogleUserInfoVO vo){
        MemberEntity entity = memberService.oauthLogin(vo);
        if(entity == null){
            return R.error(BizCodeEnum.OAUTHLOGIN_EXCEPTION.getCode(),BizCodeEnum.OAUTHLOGIN_EXCEPTION.getMessage());
        }else {
            return R.ok().setData(entity);
        }
    }
    @PostMapping("/login")
    public R login(@RequestBody LoginAccountVO vo){
        MemberEntity entity = memberService.login(vo);
        if(entity == null){
            return R.error(BizCodeEnum.LOGIN_EXCEPTION.getCode(),BizCodeEnum.LOGIN_EXCEPTION.getMessage());
        }else {
            return R.ok().setData(entity);
        }
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{mID}")
    public R info(@PathVariable("mID") Long mID){
		MemberEntity member = memberService.getById(mID);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
