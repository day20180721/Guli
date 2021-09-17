package com.littlejenny.gulimall.auth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.to.member.MemberEntityTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.auth.constants.AuthConstants;
import com.littlejenny.gulimall.auth.feign.MemberFeignService;
import com.littlejenny.gulimall.auth.utils.URLs;
import com.littlejenny.gulimall.auth.vo.LoginAccountVO;
import com.littlejenny.gulimall.auth.vo.google.GetTokenVO;
import com.littlejenny.gulimall.auth.vo.google.GoogleUserInfoVO;
import com.littlejenny.gulimall.auth.vo.google.TokenVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    AuthConstants constants;
    @Autowired
    private RestTemplate restTemplate;
    @GetMapping("/oauth/google/login")
    public String oauthlogin(){
        Map<String,String> param = new HashMap<>();
        param.put("scope",AuthConstants.GOOGLE_API_CERTIFICATION_INFO_URL);
        param.put("access_type","offline");
        param.put("include_granted_scopes","true");
        param.put("response_type","code");
        param.put("redirect_uri",AuthConstants.REDIRECT_LONIN_URL);
        param.put("client_id",constants.getClient_id());

        String url = URLs.buildUrl(AuthConstants.GOOGLE_API_CERTIFICATION_PAGE_URL,param);
        System.out.println(url);
        return "redirect:" +url;
    }

    @GetMapping("/oauth/google/loginCallback")
    public String oauthcallback(String code,String scope,HttpSession session){
        try {
            GetTokenVO getTokenParam = new GetTokenVO();
            getTokenParam.setCode(code);
            getTokenParam.setClient_id(constants.getClient_id());
            getTokenParam.setClient_secret(constants.getClient_secret());
            getTokenParam.setGrant_type(constants.getGrant_type());
            getTokenParam.setRedirect_uri(AuthConstants.REDIRECT_LONIN_URL);
            HttpEntity<GetTokenVO> request = new HttpEntity<GetTokenVO>(getTokenParam);
            TokenVO vo = restTemplate.postForObject(AuthConstants.GOOGLE_GET_TOKEN_URL,request,TokenVO.class);
            //TODO 拼接URL可以更好
            GoogleUserInfoVO userInfoVO = restTemplate.getForObject(AuthConstants.GOOGLE_API_INFO_URL+"?access_token="+vo.getAccess_token() ,GoogleUserInfoVO.class);

            R r = memberFeignService.oauthLogin(userInfoVO);
            if(r.getCode() == 0){
                MemberEntityTO user = r.getData(new TypeReference<MemberEntityTO>() {
                });
                session.setAttribute(AuthConstants.USERKEY,user);
                return "redirect:http://gulimall.com/";
            }else {
                return "redirect:http://auth.gulimall.com/";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "redirect:http://auth.gulimall.com/";
        }
    }

    @GetMapping("/login")
    public String login(LoginAccountVO loginAccountVO, HttpSession session){
        R r = memberFeignService.login(loginAccountVO);
        if(r.getCode() == 0){
            MemberEntityTO user = r.getData(new TypeReference<MemberEntityTO>() {
            });
            session.setAttribute(AuthConstants.USERKEY,user);
            return "redirect:http://gulimall.com/";
        }else {
            return "redirect:http://auth.gulimall.com/";
        }
    }

    @ResponseBody
    @GetMapping("t")
    public String t(HttpSession session)
    {
        session.setAttribute("key","value");
        return "t";
    }
}
