package com.littlejenny.gulimall.vo;

import com.littlejenny.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchRespVO {
    private List<SkuEsModel> models;
    private Integer currentPage;
    private Long totalPage;
    private Long totalRow;
    private List<Integer> pages;
    private List<BrandVO> brandVOS;
    private List<CategoryVO> categoryVOS;
    private List<AttrVO> attrs;
    private List<Long> userdAttrs = new ArrayList<>();

    private List<FactorNavVO> navVOS = new ArrayList<>();

    @Data
    public static class FactorNavVO{
        private String name;
        private String value;
        private String url;
    }
    @Data
    public static class BrandVO{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }
    @Data
    public static class CategoryVO{
        private Long catalogId;
        private String catalogName;
    }
    @Data
    public static class AttrVO {
        private Long attrID;
        private String attrName;
        private List<String> attrValue;
    }
}
