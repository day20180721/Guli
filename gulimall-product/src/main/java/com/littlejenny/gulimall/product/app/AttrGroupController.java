package com.littlejenny.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.littlejenny.gulimall.product.entity.AttrEntity;
import com.littlejenny.gulimall.product.service.AttrAttrgroupRelationService;
import com.littlejenny.gulimall.product.service.CategoryService;
import com.littlejenny.gulimall.product.vo.AttrAttrgroupRelationEntityVO;
import com.littlejenny.gulimall.product.vo.AttrGroupEntityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.littlejenny.gulimall.product.entity.AttrGroupEntity;
import com.littlejenny.gulimall.product.service.AttrGroupService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.R;



/**
 * 属性分组
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 16:20:50
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    /**
     * list改成帶catId的restful
     * service的queryPage，改成帶catId的restful
     * service的queryPage，因為前端需求檢索功能，所以要模糊查詢
     *
     * 使取得全部分類時要判斷分類的Children是否為空，如果為空則不包裝成json
     *
     * CategoryEntity增加一元素:catelogPath用以存儲及聯整體父路徑
     * 需要在CategoryService新增一個方法來提供Path
     */
    /**
     * 列表
     */
    @RequestMapping("/list/{catId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catId")Long catId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
		//注意要用attrGroup.getCatelogId()，attrGroupId是屬性分組ID，屬性分組是用來描述商品訊息的
        //不要矇了
        Long[] path = categoryService.getCategoryPath(attrGroup.getCatelogId());
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }
    /*
    跟據傳入的groupID來回顯所關聯的attr
    先從aaService中找出所有關聯的attrID
    在將這些attrID拿去attr表中查出entity
     */

    @GetMapping("/{attrgroupId}/attr/relation")
    public R listAttrRelation(@PathVariable("attrgroupId")Long groupId){
        List<AttrEntity> attrEntities = attrGroupService.getAttrsByGroupId(groupId);
        return R.ok().put("data", attrEntities);
    }
    /*

     */
    @PostMapping("/attr/relation")
    public R saveAttrRelation(@RequestBody AttrAttrgroupRelationEntityVO[] entitys){
        attrAttrgroupRelationService.saveBatchByVOs(Arrays.asList(entitys));
        return R.ok();
    }
    /*

     */
    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody AttrAttrgroupRelationEntityVO[] vos){
        attrAttrgroupRelationService.deleteBatchRelation(vos);
        return R.ok();
    }
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noattrRelation(@PathVariable("attrgroupId")Long selfGroupId,@RequestParam Map<String, Object> params){
        PageUtils pageUtils = attrAttrgroupRelationService.listNoattrRelation(selfGroupId, params);
        return R.ok().put("page",pageUtils);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
//		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        attrGroupService.removeDetailByIds(Arrays.asList(attrGroupIds));
        return R.ok();
    }

    @RequestMapping("{catelogId}/withattr")
    public R getGroupwithattrByCatId(@PathVariable("catelogId")Long catId){
        List<AttrGroupEntityVO> vos = attrGroupService.getGroupwithattrByCatId(catId);
        return R.ok().put("data",vos);
    }
    //TODO Vue引入el-divider
}
