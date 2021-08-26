package com.littlejenny.gulimall.cart.vo;

import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
@ToString
public class CartVO {
    List<CartItemVO> items;
    private BigDecimal reduce;
    public List<CartItemVO> getItems() {
        return items;
    }

    public void setItems(List<CartItemVO> items) {
        this.items = items;
    }

    public Integer getItemTotalCount() {
        Integer itemTotalCount = 0;
        for (CartItemVO item : items) {
            itemTotalCount += item.getCount();
        }
        return itemTotalCount;
    }


    public Integer getItemTypeCount() {
        if(items != null){
            return items.size();
        }
        return 0;
    }


    public BigDecimal getTotalPrice() {
        BigDecimal t = new BigDecimal(0);
        for (CartItemVO item : items) {
            if(item.getCheck()){
                t = t.add(item.getTotalPrice());
            }
        }
        return t;
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
