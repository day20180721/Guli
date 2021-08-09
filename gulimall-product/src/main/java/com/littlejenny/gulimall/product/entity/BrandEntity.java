package com.littlejenny.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.littlejenny.common.validanno.ListVal;
import com.littlejenny.common.validgroup.AddGroup;
import com.littlejenny.common.validgroup.UpdateGroup;
import com.littlejenny.common.validgroup.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 15:11:54
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/*在Update時如果沒有傳值則默認不改的情況下，我只會在必要參數做更改
	* ID(為空則會SQL報錯)
	* URL(正確解析)
	* Status(限制值)
	* firstLetter(限制必須為一個英文單字)
	* sort(限制值)
	*/

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改必須指定品牌ID",groups = {UpdateGroup.class})
	@Null(message = "增加必須時品牌ID必須為空",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名是必要的",groups = {AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotEmpty(message = "品牌URL不能為空",groups = {AddGroup.class})
	@URL(message = "品牌URL必須正確",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	//其他的認證在沒傳值的情況下都會過，但是我這個好像不行
	@NotNull(message = "顯示狀態不能為空",groups = {AddGroup.class})
	@ListVal(vals = {1,0},message = "顯示狀態必須為0/1",groups = {AddGroup.class, UpdateStatusGroup.class,UpdateGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(message = "字首查詢不能為空",groups = {AddGroup.class})
	//不用+//
	@Pattern(regexp = "^[A-z]$",message = "檢索字母必須為單個英文字母",groups = {AddGroup.class,UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "字首查詢不能為空",groups = {AddGroup.class})
	@Max(value = 20,message = "順序必須為0-20",groups = {AddGroup.class,UpdateGroup.class})
	@Min(value = 0,message = "順序必須為0-20",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
