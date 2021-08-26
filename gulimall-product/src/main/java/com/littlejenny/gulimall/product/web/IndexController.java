package com.littlejenny.gulimall.product.web;

import com.littlejenny.gulimall.product.entity.CategoryEntity;
import com.littlejenny.gulimall.product.service.CategoryService;
import com.littlejenny.gulimall.product.vo.Catelog2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping({"/index.html","/"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryEntityList = categoryService.getAllLevelOne();
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Catelog2VO>> getCatalogJson(){
        Map<String, List<Catelog2VO>> json =  categoryService.getCatalogJson();
        return json;
    }
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        System.out.println("Hello");
        return "Hello";
    }
}
