package com.littlejenny.gulimall.cart.vo;

import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
@ToString
public class CartItemVO {

    private Long skuId;
    private Boolean check = true;
    private String title;
    private String imageUrl;
    private List<String> skuAttr; //key value合併自段
    private BigDecimal price;
    private Integer count;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    public void addCount(Integer count){
        this.count += count;
    }
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(count));
    }
}
