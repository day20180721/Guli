package com.littlejenny.gulimall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.littlejenny.gulimall.product.vo.addproduct.SpuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.littlejenny.gulimall.product.entity.SpuInfoEntity;
import com.littlejenny.gulimall.product.service.SpuInfoService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.R;



/**
 * spu信息
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 16:20:50
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;
    //http://localhost:88/api/product/spuinfo/1/up
    @PostMapping("/{spuId}/up")
    public R upSpuById(@PathVariable("spuId")Long spuId){
        spuInfoService.upSpuById(spuId);
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = spuInfoService.queryPage(params);
        PageUtils page = spuInfoService.queryByCidBidKeyStatus(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody SpuVO Spuvo){
//		spuInfoService.save(spuInfo);
        spuInfoService.saveDetail(Spuvo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
