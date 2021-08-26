package com.littlejenny.gulimall.order.vo;

import com.littlejenny.common.to.HasStockTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderConfirmVO {
    private List<MemberReceiveAddressVO> addrs;
    private List<CartItemVO> items;
    private Integer integration;
    private BigDecimal payPrice;
    private Map<Long, HasStockTO> hasStockMap;

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    private String orderSn;

    public List<MemberReceiveAddressVO> getAddrs() {
        return addrs;
    }

    public void setAddrs(List<MemberReceiveAddressVO> addrs) {
        this.addrs = addrs;
    }

    public List<CartItemVO> getItems() {
        return items;
    }

    public void setItems(List<CartItemVO> items) {
        this.items = items;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public BigDecimal getItemsPrice() {
        BigDecimal itemsPrice = new BigDecimal(0);
        if(items != null && items.size() > 0){
            for (CartItemVO item : items) {
                itemsPrice = itemsPrice.add(item.getTotalPrice());
            }
        }
        return itemsPrice;
    }

    public Integer getItemsCount(){
        int count = 0;
        if(items != null && items.size() > 0){
            for (CartItemVO item : items) {
                count += item.getCount();
            }
        }
        return count;
    }
    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public Map<Long, HasStockTO> getHasStockMap() {
        return hasStockMap;
    }

    public void setHasStockMap(Map<Long, HasStockTO> hasStockMap) {
        this.hasStockMap = hasStockMap;
    }
}
