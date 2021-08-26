package com.littlejenny.gulimall.controller;


import com.littlejenny.common.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/thirdPart/sms")
public class SMSController {
    @GetMapping("/sendSms")
    public R sendSms(@RequestParam("phone") String phone,@RequestParam("code") String code){
        System.out.println("SendSMS:" + phone +",Code:"+code);
        return R.ok();
    }
}
