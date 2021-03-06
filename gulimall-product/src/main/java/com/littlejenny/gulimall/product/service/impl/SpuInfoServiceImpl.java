package com.littlejenny.gulimall.product.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.constant.ProductConstants;
import com.littlejenny.common.to.coupon.MemberPriceTO;
import com.littlejenny.common.to.es.SkuEsModel;
import com.littlejenny.common.to.coupon.SkuLadderTO;
import com.littlejenny.common.to.coupon.SkufullReductionTO;
import com.littlejenny.common.to.coupon.SpuBoundsTO;
import com.littlejenny.common.to.ware.HasStockTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.common.utils.StringUitls;
import com.littlejenny.gulimall.product.entity.*;
import com.littlejenny.gulimall.product.feign.CouponService;
import com.littlejenny.gulimall.product.feign.ElasticService;
import com.littlejenny.gulimall.product.feign.WareService;
import com.littlejenny.gulimall.product.service.*;
import com.littlejenny.gulimall.product.vo.addproduct.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SkuImagesService skuImagesService;
    //????????????????????????
    @Transactional
    @Override
    public void saveDetail(SpuVO spuvo) {
        //1.???????????????????????????spuinfo
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuvo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();
        //2.?????????????????????????????????pms_spu_info_desc;
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        String decript = StringUitls.combineByDelimiter(",",  spuvo.getDecript());
        spuInfoDescEntity.setDecript(decript);
        spuInfoDescService.save(spuInfoDescEntity);
        //3.???????????????????????????pms_spu_images;
        List<String> spuImages = spuvo.getImages();
        List<SpuImagesEntity> spuImagesEntities = spuImages.stream().map(item -> {
            SpuImagesEntity imagesEntity = new SpuImagesEntity();
            String imgName = item.substring(item.lastIndexOf("/") + 1);
            imagesEntity.setImgName(imgName);
            imagesEntity.setSpuId(spuId);
            imagesEntity.setImgUrl(item);
            return imagesEntity;
        }).collect(Collectors.toList());
        spuImagesService.saveBatch(spuImagesEntities);
        //4.????????????????????????????????????????????????????????????coupon??????sms_spu_bounds //????????????spuID private Long spuId;
        Bounds bounds = spuvo.getBounds();
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        BeanUtils.copyProperties(bounds,spuBoundsTO);
        spuBoundsTO.setSpuId(spuId);
        couponService.saveBound(spuBoundsTO);
        //5.???????????????????????????????????????????????????pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuvo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(item.getAttrId());
            productAttrValueEntity.setAttrValue(item.getAttrValues());
            productAttrValueEntity.setQuickShow(item.getShowDesc());
            productAttrValueEntity.setSpuId(spuId);
            AttrEntity attrEntity = attrService.getById(item.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntities);

        //6.??????Sku???????????????pms_sku_info
        List<Skus> skus = spuvo.getSkus();
        for (Skus sku : skus) {
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku,skuInfoEntity);
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setBrandId(spuvo.getBrandId());
            skuInfoEntity.setCatalogId(spuvo.getCatalogId());
            List<Images> skuImages_ = sku.getImages();
            for (Images images : skuImages_) {
                if(images.getDefaultImg() == 1){
                    skuInfoEntity.setSkuDefaultImg(images.getImgUrl());
                    break;
                }
            }
            skuInfoService.save(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();

            //7.??????Sku?????????????????????????????????????????????pms_sku_sale_attr_value
            List<Attr> attrs = sku.getAttr();
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(item -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(item, skuSaleAttrValueEntity);
                skuSaleAttrValueEntity.setSkuId(skuId);
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

            //????????????????????????????????????
            //8.??????Sku???????????????pms_sku_images
            List<Images> skuImages = sku.getImages();
            List<SkuImagesEntity> imagesEntities = skuImages.stream().filter(item ->{
                return !"".equals(item.getImgUrl());
            }).map(item -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                BeanUtils.copyProperties(item, skuImagesEntity);
                skuImagesEntity.setSkuId(skuId);
                return skuImagesEntity;
            }).collect(Collectors.toList());
            skuImagesService.saveBatch(imagesEntities);


            //9.??????Sku(?????????????)???(?????????????)???coupon??????(sms_sku_full_reduction)(sms_sku_ladder)
            if(sku.getFullCount() !=0 && sku.getDiscount().compareTo(BigDecimal.ZERO) == 1){
                SkuLadderTO skuLadderTO = new SkuLadderTO();
                skuLadderTO.setSkuId(skuId);
                skuLadderTO.setFullCount(sku.getFullCount());
                skuLadderTO.setDiscount(sku.getDiscount());
                skuLadderTO.setAddOther(sku.getCountStatus());
                BigDecimal afterDiscountPrice = sku.getDiscount().multiply(sku.getPrice());
                skuLadderTO.setPrice(afterDiscountPrice);
                couponService.saveSkuladder(skuLadderTO);
            }
            if(sku.getFullPrice().compareTo(BigDecimal.ZERO) == 1 && sku.getReducePrice().compareTo(BigDecimal.ZERO) == 1){
                SkufullReductionTO skufullReductionTO = new SkufullReductionTO();
                skufullReductionTO.setSkuId(skuId);
                skufullReductionTO.setFullPrice(sku.getFullPrice());
                skufullReductionTO.setReducePrice(sku.getReducePrice());
                skufullReductionTO.setAddOther(sku.getPriceStatus());
                couponService.saveSkufullreduction(skufullReductionTO);
            }

            //10.?????????????????????????????????????????????coupon??????sms_member_price
            List<MemberPrice> memberPrice = sku.getMemberPrice();
            List<MemberPriceTO> memberPriceTOS = memberPrice.stream().filter(item ->{
                return item.getPrice().compareTo(BigDecimal.ZERO) == 1;
            }).map(item -> {
                MemberPriceTO memberPriceTO = new MemberPriceTO();
                memberPriceTO.setMemberPrice(item.getPrice());
                memberPriceTO.setMemberLevelId(item.getId());
                memberPriceTO.setMemberLevelName(item.getName());
                memberPriceTO.setSkuId(skuId);
                return memberPriceTO;
            }).collect(Collectors.toList());
            couponService.saveMemberPrice(memberPriceTOS);
        }
    }

    /**
     * status: 0
     * key: 2
     * brandId: 20
     * catelogId: 225
     */
    @Override
    public PageUtils queryByCidBidKeyStatus(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String status = (String)params.get("status");
        if(status != null && status.matches("^[0-2]$")){
            wrapper.eq("publish_status",status);
        }
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w ->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        String brandId = (String)params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && brandId.matches("^[1-9]\\d*$")){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String)params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && catelogId.matches("^[1-9]\\d*$")){
            wrapper.eq("catalog_id",catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private WareService wareService;
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private SpuInfoDao spuInfoDao;
    @Autowired
    private ElasticService elasticService;
    @Transactional
    @Override
    public void upSpuById(Long spuId) {
        //?????????SPU??????????????????SKU
        QueryWrapper<SkuInfoEntity> skuWrapper = new QueryWrapper<>();
        skuWrapper.eq("spu_id",spuId);
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.list(skuWrapper);
        //??????????????????Sku????????????????????????????????????Sku???ID
        List<Long> skuIdlist = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        R r = wareService.hasStockByIds(skuIdlist);
        TypeReference<Map<Long, HasStockTO>> toTypeReference = new TypeReference<Map<Long,HasStockTO>>() {};
        Map<Long,HasStockTO> stockData = r.getData(toTypeReference);
        //???SPU??????????????????????????????????????????????????????????????????
        List<ProductAttrValueEntity> attrsBySpuId = productAttrValueService.getAttrsBySpuId(spuId);
        List<Long> unFilterAttrsIds = attrsBySpuId.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long> filterAttrsIds = attrService.filterSearchable(unFilterAttrsIds);
        List<SkuEsModel.Attr> attrs = attrsBySpuId.stream().filter(item -> {
            return filterAttrsIds.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attr attr = new SkuEsModel.Attr();
            BeanUtils.copyProperties(item, attr);
            return attr;
        }).collect(Collectors.toList());

        //??????SkuElaticSearch????????????
        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(item -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(item, skuEsModel);
            skuEsModel.setSkuImg(item.getSkuDefaultImg());//????????????elastic?????????????????????????????????????????????
            skuEsModel.setSkuPrice(item.getPrice());//??????
            //??????CatlogID???brandID?????????
            CategoryEntity category = categoryService.getById(item.getCatalogId());
            skuEsModel.setCatalogName(category.getName());
            BrandEntity brand = brandService.getById(item.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            //??????BrandImage

            //?????????????????????????????????????????????0
            skuEsModel.setHotScore(0L);
            //?????????????????????
            //??????????????????????????????????????????????????????
            //????????????????????????TrueOrFalse??????????????????null
            HasStockTO hasStockTO = stockData.get(item.getSkuId());
            skuEsModel.setHasStock(hasStockTO.getHasStock());
            //????????????SPU??????
            //??????????????????????????????????????????
            skuEsModel.setAttrs(attrs);
            return skuEsModel;
        }).collect(Collectors.toList());
        //?????????elasticSearch
        R elasticResult = elasticService.save(skuEsModels);
        if(elasticResult.getCode() == 0){
            //??????????????????publish_status????????????
            spuInfoDao.updatePublishStatus(spuId, ProductConstants.SpuPublishStatus.ON.getCode());
        }
    }
}