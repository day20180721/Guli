package com.littlejenny.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/confirm.html")
    public String confirm(){
        return "confirm";
    }
    @GetMapping("/detail.html")
    public String detail(){
        return "detail";
    }
    @GetMapping("/list.html")
    public String list(){
        return "list";
    }
    @GetMapping("/pay.html")
    public String pay(){
        return "pay";
    }
}
