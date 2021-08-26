package com.littlejenny.gulimall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class AttrGroupEntityVO {
    private static final long serialVersionUID = 1L;
    private Long attrGroupId;
    private String attrGroupName;
    private Integer sort;
    private String descript;
    private String icon;
    private Long catelogId;
    private List<AttrVO> attrs;

}
