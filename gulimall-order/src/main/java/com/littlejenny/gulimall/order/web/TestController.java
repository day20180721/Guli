package com.littlejenny.gulimall.order.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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


    @SentinelResource("demo_product_info_hot")
    @ResponseBody
    @GetMapping(value = "/order/order/info/sn/t")
    public String t(){
        return "<html>\n" +
                "<head>\n" +
                "    <title>新分頁</title>\n" +
                "   \n" +
                "</head>\n" +
                "\t<body>\n" +
                "\t\t<b>你好</b>\n" +
                "\t</body>\n" +
                "</html>";
    }
}
