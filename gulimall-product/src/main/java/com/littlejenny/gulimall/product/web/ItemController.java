package com.littlejenny.gulimall.product.web;

import com.littlejenny.gulimall.product.service.ItemService;
import com.littlejenny.gulimall.product.vo.item.ItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId")Long skuid, Model model) throws ExecutionException, InterruptedException {
        ItemVO item = itemService.item(skuid);
        model.addAttribute("item",item);
        System.out.println("正在查詢 " + skuid+" 的詳細資訊");
        return "item";
    }
}
