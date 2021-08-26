package com.littlejenny.gulimall.vo;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
public class SearchParamVO {
    private Long catalog3Id;
    private List<Long> brandIDs;
    private String keyword;
    private String sort;//自定義字串需要自己拆分
    private List<String> attrs;
    private Integer hasStock;//只顯示有貨
    private String priceRange;
    private Integer page = 1;//頁碼

    private String queryFullString;

}
