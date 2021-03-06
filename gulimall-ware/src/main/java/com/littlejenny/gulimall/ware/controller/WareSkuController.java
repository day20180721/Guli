package com.littlejenny.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.common.to.ware.HasStockTO;
import com.littlejenny.common.exception.ware.NoWareCanHandleSkuException;
import com.littlejenny.gulimall.ware.to.SubtarctStockTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.littlejenny.gulimall.ware.entity.WareSkuEntity;
import com.littlejenny.gulimall.ware.service.WareSkuService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.R;



/**
 * 商品库存
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:35:23
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;
    @RequestMapping("/lockStocks")
    R subTractStock(@RequestBody SubtarctStockTO subtarctStockTO){
        try {
            wareSkuService.lockStocks(subtarctStockTO);
            return R.ok();
        } catch (NoWareCanHandleSkuException e) {
            String msg = ",錯誤商品編號為:" + e.getSkuId();
            e.printStackTrace();
            return R.error(BizCodeEnum.NOWARECANHANDLE_EXCEPTION.getCode(),BizCodeEnum.NOWARECANHANDLE_EXCEPTION.getMessage()+msg);
        }
    }
    @RequestMapping("/hasStockById")
    public R hasStockByIds(@RequestBody List<Long> skuIds){
        Map<Long, HasStockTO> map = wareSkuService.hasStockByIds(skuIds);
        return R.ok().setData(map);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = wareSkuService.queryPage(params);
        PageUtils page = wareSkuService.queryPageBySkuIdWareId(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
