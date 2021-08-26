package com.littlejenny.gulimall.product.vo.item;

import lombok.Data;

import java.util.List;

@Data
public class SkuAttrGroupVO {
    private Long attrId;
    private String attrName;
    private List<SkuAttrVO> attrVOList;
}
