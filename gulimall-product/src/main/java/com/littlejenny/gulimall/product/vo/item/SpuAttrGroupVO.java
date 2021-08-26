package com.littlejenny.gulimall.product.vo.item;

import lombok.Data;

import java.util.List;

@Data
public class SpuAttrGroupVO {
    private String attrGroupName;
    private List<SpuAttrVO> attrVOS;
}
