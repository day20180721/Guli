package com.littlejenny.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.littlejenny.gulimall.product.entity.ProductAttrValueEntity;
import com.littlejenny.gulimall.product.vo.AttrResponseVO;
import com.littlejenny.gulimall.product.vo.AttrVO;
import com.littlejenny.gulimall.product.vo.AttrValueVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.littlejenny.gulimall.product.entity.AttrEntity;
import com.littlejenny.gulimall.product.service.AttrService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.R;



/**
 * 商品属性
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 16:20:50
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    ///product/attr/base/listforspu/{spuId}
    @GetMapping("/base/listforspu/{spuId}")
    public R listforspu(@PathVariable("spuId")Long spuId){
        List<ProductAttrValueEntity> data = attrService.getKVbySpuId(spuId);
        return R.ok().put("data", data);
    }
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);
        return R.ok().put("page", page);
    }
    @RequestMapping("/{type}/list/{catId}")
    public R listbyIdAndType(@RequestParam Map<String, Object> params,
                             @PathVariable(value = "catId",required = false)Long catId,
                             @PathVariable(value = "type",required = false)String type){
        PageUtils page = attrService.queryPageByCatId(params,catId,type);
        return R.ok().put("page", page);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
        AttrResponseVO vo = attrService.getDetailById(attrId);
        return R.ok().put("attr", vo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVO attr){
		attrService.save(attr);
        return R.ok();
    }
    ///product/attr/update/{spuId}
    @RequestMapping("/update/{spuId}")
    public R updateAll(@PathVariable("spuId")Long spuId, @RequestBody List<AttrValueVO> vos){
        attrService.updateAllBySpuId(spuId,vos);
        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrResponseVO attr){
//		attrService.updateById(attr);
        attrService.updateDetailById(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R deletes(@RequestBody Long[] attrIds){
        //刪除attr要順便更改relation
//		attrService.removeByIds(Arrays.asList(attrIds));
        attrService.removeDetailByIds(Arrays.asList(attrIds));
        return R.ok();
    }
}
