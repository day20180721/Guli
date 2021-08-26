package com.littlejenny.gulimall.cart.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class TestController {
    @ResponseBody
    @GetMapping("/t_hello")
    public String hello(){
        return "hello";
    }
    @GetMapping("/t_cart")
    public String cart(){
        return "cart";
    }
    @GetMapping("/t_success")
    public String success(){
        return "success";
    }
}
