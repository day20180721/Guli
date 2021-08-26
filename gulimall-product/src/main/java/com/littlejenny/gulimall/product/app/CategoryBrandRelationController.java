package com.littlejenny.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.littlejenny.gulimall.product.vo.CategoryBrandRelationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.littlejenny.gulimall.product.entity.CategoryBrandRelationEntity;
import com.littlejenny.gulimall.product.service.CategoryBrandRelationService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 16:20:50
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @RequestMapping("/brands/list")
    public R brandsList(@RequestParam Long catId){
//        PageUtils page = categoryBrandRelationService.queryPage(params);
        List<CategoryBrandRelationVO> data = categoryBrandRelationService.getAllByCatId(catId);
        return R.ok().put("data", data);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("catelog/list")
    public R catelogList(@RequestParam Long brandId){
//        PageUtils page = categoryBrandRelationService.queryPage(params);
        CategoryBrandRelationEntity[] entities = categoryBrandRelationService.catelogList(brandId);
        /*
        透過傳來的BrandID找到此Brand所關聯的分類名稱
         */
        return R.ok().put("data", entities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }
    /**
     * 目前只會傳brandID及categoryID，而我要用這兩個屬性分別在對應的資料庫找到名稱，然後再存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
//		categoryBrandRelationService.save(categoryBrandRelation);
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 目前只會傳brandID及categoryID，而我要用這兩個屬性分別在對應的資料庫找到名稱，然後再存
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
//		categoryBrandRelationService.updateById(categoryBrandRelation);
        categoryBrandRelationService.updateDetailById(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
