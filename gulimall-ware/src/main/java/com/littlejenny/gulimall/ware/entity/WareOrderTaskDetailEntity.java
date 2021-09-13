package com.littlejenny.gulimall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 库存工作单
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:35:23
 */
@Data
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
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
	 * (X)工作单id
	 * (0)改成OrderSn了
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
