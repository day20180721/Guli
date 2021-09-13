package com.littlejenny.common.to.ware;

import lombok.Data;

@Data
public class WareOrderTaskDetailTO {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private String orderSn;
    /**
     * 此工作單細節的處理狀況
     * 1.鎖定 ->下單還沒付款
     * 2.解鎖 ->下單過期、取消訂單、解決下單的分布式回滾，
     * 		狀況為:下單途中調用鎖定庫存方法，下單後續流程因故回滾，然而遠程庫存卻沒有解鎖
     * 3.扣除 ->付款
     *
     */
    private Integer taskState;
    private Long wareId;
}
