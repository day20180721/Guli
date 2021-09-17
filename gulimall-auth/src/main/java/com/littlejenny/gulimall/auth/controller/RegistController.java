package com.littlejenny.gulimall.auth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.common.to.member.MemberEntityTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.auth.constants.AuthConstants;
import com.littlejenny.gulimall.auth.exception.SMSCDException;
import com.littlejenny.gulimall.auth.feign.MemberFeignService;
import com.littlejenny.gulimall.auth.service.impl.RegistServiceImpl;
import com.littlejenny.gulimall.auth.vo.RegistAccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RegistController {
    @Autowired
    private MemberFeignService memberFeignService;

    @PostMapping("/regist")
    public String regist(@Valid RegistAccountVO vo, BindingResult result, HttpSession session){
        if(result.hasErrors()){
            Map<String, Object> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),fieldError.getDefaultMessage());
            }
            session.setAttribute("frontErrors",errors);
            return "redirect:http://auth.gulimall.com/regist.html";
        }else {
            //判斷驗證碼是否正確
            if(!registService.matchCode(vo.getPhone(),vo.getCode())){
                session.setAttribute("backError","驗證碼錯誤/超時");
                return "redirect:http://auth.gulimall.com/regist.html";
            }
            //將資料傳送給Member服務
            R r = memberFeignService.regist(vo);
            if(r.getCode() == 0){
                MemberEntityTO user = r.getData(new TypeReference<MemberEntityTO>() {
                });
                session.setAttribute(AuthConstants.USERKEY,user);
                registService.removeCode(vo.getPhone());
                return "redirect:http://gulimall.com";
            }else {
                session.setAttribute("backError",r.getValue("msg", new TypeReference<String>() {}));
                return "redirect:http://auth.gulimall.com/regist.html";
            }
        }
    }
    @Autowired
    private RegistServiceImpl registService;
    @ResponseBody
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone")String phone){
        try {
            registService.sendCode(phone);
        }catch (SMSCDException e){
            return R.error(BizCodeEnum.SMSCD_EXCEPTION.getCode(), BizCodeEnum.SMSCD_EXCEPTION.getMessage());
        }
        return  R.ok();
    }
}
