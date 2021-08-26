package com.littlejenny.gulimall.product.vo.item;

import com.littlejenny.gulimall.product.entity.*;
import lombok.Data;

import java.util.List;

@Data
public class ItemVO {
    private SkuInfoEntity skuInfoEntity;

    private List<SkuImagesEntity> skuImagesEntities;

    private List<String> spuInfoDescEntities;

    private List<SpuAttrGroupVO> spuAttrGroupVOS;

    private List<SkuAttrGroupVO> skuAttrGroupVOS;
}
