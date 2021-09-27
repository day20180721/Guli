package com.littlejenny.gulimall.order.web;

import com.littlejenny.gulimall.order.service.OrderService;
import com.littlejenny.gulimall.order.vo.orders.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders.html")
    public String orders(@RequestParam(value = "pageNo",defaultValue = "0")String pageNo, Model model){
        Map<String, Object> params = new HashMap<>();
        params.put("page",pageNo);
        params.put("limit",15);
        params.put("sidx","id");
        params.put("order","DESC");
        List<OrderVO> vo = orderService.queryPageWithItem(pageNo,params);
        model.addAttribute("orders",vo);
        return "list";
    }
}
