package com.littlejenny.gulimall.order.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItemVO {
    private Long skuId;
    private Boolean check = true;
    private String title;
    private String imageUrl;
    private BigDecimal price;
    private Integer count;
    private List<String> skuAttr; //key value合併自段

    @JsonIgnore
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(count));
    }
}
