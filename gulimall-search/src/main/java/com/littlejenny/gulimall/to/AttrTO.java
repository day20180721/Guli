package com.littlejenny.gulimall.to;

import lombok.Data;

@Data
public class AttrTO {
    private String catelogName;
    private String groupName;
    private Long[] catelogPath;
    private Long attrId;
    private String attrName;
    private Integer searchType;
    private String icon;
    private String valueSelect;
    private Integer attrType;
    private Long enable;
    private Long catelogId;
    private Integer showDesc;
    private Long attrGroupId;
}
