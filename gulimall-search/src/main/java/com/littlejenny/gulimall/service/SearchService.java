package com.littlejenny.gulimall.service;

import com.littlejenny.gulimall.vo.SearchParamVO;
import com.littlejenny.gulimall.vo.SearchRespVO;

public interface SearchService {
    SearchRespVO search(SearchParamVO param);
}
