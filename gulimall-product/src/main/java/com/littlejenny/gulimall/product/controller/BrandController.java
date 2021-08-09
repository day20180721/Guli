package com.littlejenny.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.littlejenny.common.validgroup.AddGroup;
import com.littlejenny.common.validgroup.UpdateGroup;
import com.littlejenny.common.validgroup.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.littlejenny.gulimall.product.entity.BrandEntity;
import com.littlejenny.gulimall.product.service.BrandService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 16:20:50
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    /*
    開啟分頁Mybatis插件
    啟用模糊查詢
     */
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 方法一
     *  要驗證的Bean後面緊跟result，可以取得結果，如果不加會直接報一大頁的400錯誤，加了我們能夠用我
     *  們原本的方法
     * 方法二
     *  使用統一處理異常類來處理驗證異常，處理異常類位於exception.GulimallExceptionControllerAdvice
     */
    @RequestMapping("/save")
    public R save(@Validated(value={AddGroup.class}) @Valid @RequestBody BrandEntity brand/*, BindingResult result*/){
//        if(result.hasErrors()){
//            Map errorField = new HashMap();
//            result.getFieldErrors().forEach((item)->{
//                //獲取到錯誤的提示
//                String message = item.getDefaultMessage();
//                //獲取到錯誤的屬性名稱
//                String field = item.getField();
//                errorField.put(field,message);
//            });
//            return R.error(400,"提交的數據不合法").put("data",errorField);
//        }else {
//
//        }
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated(value={UpdateGroup.class}) @Valid @RequestBody BrandEntity brand){
		//如果只有傳遞ID進來，會因為UPDATE pms_brand WHERE brand_id=?造成語法錯誤，因為沒有set的值
        //update時必須同時維護categoryBrandRelation的名稱
//        brandService.updateById(brand);
        brandService.updateDetailByID(brand);
        return R.ok();
    }
    @RequestMapping("/update/status")
    public R updateStatus(@Validated(value={UpdateStatusGroup.class}) @Valid @RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
