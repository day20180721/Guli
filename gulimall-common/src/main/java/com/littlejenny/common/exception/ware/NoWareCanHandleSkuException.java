package com.littlejenny.common.exception.ware;

public class NoWareCanHandleSkuException extends  RuntimeException{
    private Long skuId;
    public NoWareCanHandleSkuException(Long skuId) {
        super("沒有任何倉庫有ID為:"+skuId+"的商品");
        this.skuId = skuId;
    }
    public Long getSkuId() {
        return skuId;
    }
}
