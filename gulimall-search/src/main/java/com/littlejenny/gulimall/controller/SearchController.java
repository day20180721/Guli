package com.littlejenny.gulimall.controller;

import com.littlejenny.gulimall.service.SearchService;
import com.littlejenny.gulimall.vo.SearchParamVO;
import com.littlejenny.gulimall.vo.SearchRespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;
    @GetMapping(path = {"/search.html"})
    public String search(SearchParamVO param, Model model, HttpServletRequest request){
        param.setQueryFullString(request.getQueryString());
        SearchRespVO resp =  searchService.search(param);
        model.addAttribute("result",resp);
        return "index";
    }
    @GetMapping(path = {"/"})
    public String emptySearch(Model model, HttpServletRequest request){
        SearchParamVO param = new SearchParamVO();
        param.setQueryFullString(request.getQueryString());
        return search(new SearchParamVO(),model,request);
    }
}
