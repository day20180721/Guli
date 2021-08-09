package com.littlejenny.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.littlejenny.gulimall.ware.vo.DoneVO;
import com.littlejenny.gulimall.ware.vo.PurchaseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.littlejenny.gulimall.ware.entity.PurchaseEntity;
import com.littlejenny.gulimall.ware.service.PurchaseService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.R;



/**
 * 采购信息
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:35:23
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;
    //Done
    @PostMapping("/done")
    public R done(@RequestBody DoneVO vo){
        purchaseService.done(vo);
        return R.ok();
    }
    //received
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){
        purchaseService.received(ids);
        return R.ok();
    }
    //merge
    @PostMapping("/merge")
    public R merge(@RequestBody PurchaseVO vo){
        purchaseService.merge(vo);
        return R.ok();
    }
    //unreceive
    @RequestMapping("/unreceive/list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceiveList(params);

        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
