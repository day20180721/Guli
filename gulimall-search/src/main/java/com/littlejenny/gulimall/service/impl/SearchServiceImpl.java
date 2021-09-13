package com.littlejenny.gulimall.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.to.es.SkuEsModel;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.config.GulimallElasticSearchConfig;
import com.littlejenny.gulimall.constant.ElasticConstant;
import com.littlejenny.gulimall.feign.FeignProductService;
import com.littlejenny.gulimall.service.SearchService;
import com.littlejenny.gulimall.to.AttrTO;
import com.littlejenny.gulimall.to.BrandTO;
import com.littlejenny.gulimall.to.CategoryTO;
import com.littlejenny.gulimall.vo.SearchParamVO;
import com.littlejenny.gulimall.vo.SearchRespVO;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    public FeignProductService productService;
    @Override
    public SearchRespVO search(SearchParamVO param) {
        //1.準備查詢後的回傳結果
        SearchRespVO result = new SearchRespVO();
        //2.準備檢索請求
        SearchRequest request = buildReq(param,result);
        try{
            //3.執行檢索
            SearchResponse response = client.search(request, GulimallElasticSearchConfig.COMMON_OPTIONS);
            //4.封裝查詢結果
            handleResp(response,param,result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private SearchRequest buildReq(SearchParamVO param, SearchRespVO result) {
        //Match:key
        //Filter:sort,range,brand,category
        //建構器
        SearchSourceBuilder dslBuilder = new SearchSourceBuilder();
        //Query裡面的單元有分為邏輯單元、操作單元
        //邏輯單元
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //Key
        if(!StringUtils.isEmpty(param.getKeyword())){
            //操作單元
            MatchQueryBuilder skuTitle = QueryBuilders.matchQuery("skuTitle", param.getKeyword());
            boolQueryBuilder.must(skuTitle);
        }
        //category
        if(param.getCatalog3Id() != null){
            //term用來非text等類型
            TermQueryBuilder catalogId = QueryBuilders.termQuery("catalogId", param.getCatalog3Id());
            SearchRespVO.FactorNavVO factorNavVO = new SearchRespVO.FactorNavVO();
            factorNavVO.setName("分類");
            factorNavVO.setValue(param.getCatalog3Id().toString());
            //用ID找值 分類:手機
            R r =  productService.catelogInfo(param.getCatalog3Id());
            if(r.getCode() == 0){
                CategoryTO data = r.getData(new TypeReference<CategoryTO>() {
                });
                factorNavVO.setValue(data.getName());
            }
            result.getNavVOS().add(factorNavVO);
            boolQueryBuilder.filter(catalogId);
        }
        //brand
        if(param.getBrandIDs() != null && param.getBrandIDs().size() > 0){
            TermsQueryBuilder brandId = QueryBuilders.termsQuery("brandId", param.getBrandIDs());
            R r = productService.brandInfos(param.getBrandIDs());
            if(r.getCode() == 0){
                List<BrandTO> brandTOS = r.getValue("brand", new TypeReference<List<BrandTO>>() {
                });

                for (BrandTO brand : brandTOS) {
                    SearchRespVO.FactorNavVO factorNavVO = new SearchRespVO.FactorNavVO();
                    factorNavVO.setName("品牌");
                    //用ID找值 品牌:小米
                    factorNavVO.setValue(brand.getName());

                    String replace = queryFullStringReplace(param.getQueryFullString(), "brandIDs", brand.getBrandId().toString());
                    String fullUrl = getQueryFullString(replace);
                    factorNavVO.setUrl(fullUrl);

                    result.getNavVOS().add(factorNavVO);
                }
            }else {
                for (Long brand : param.getBrandIDs()) {
                    SearchRespVO.FactorNavVO factorNavVO = new SearchRespVO.FactorNavVO();
                    factorNavVO.setName("品牌");
                    //用ID找值 品牌:小米
                    factorNavVO.setValue(brand.toString());

                    String replace = queryFullStringReplace(param.getQueryFullString(), "brandIDs", brand.toString());
                    String fullUrl = getQueryFullString(replace);
                    factorNavVO.setUrl(fullUrl);

                    result.getNavVOS().add(factorNavVO);
                }
            }
            boolQueryBuilder.filter(brandId);
        }
        //hasStock
        if(param.getHasStock() != null){
            TermQueryBuilder hasStock = QueryBuilders.termQuery("hasStock", param.getHasStock() == 1 ? true : false);
            boolQueryBuilder.filter(hasStock);
        }
        //priceRange
        if(!StringUtils.isEmpty(param.getPriceRange())){
            RangeQueryBuilder skuPrice = range("skuPrice",param.getPriceRange());

            boolQueryBuilder.filter(skuPrice);
        }
        //attr
        if(param.getAttrs() != null && param.getAttrs().size() > 0){
            //外圈有多個Nest
            //因為不可能有Attr同時等於ID15又等於ID16
            for (String attr : param.getAttrs()) {
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                //attrs=1_5寸:8寸
                //個別屬性的多值
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                //該屬性
                query.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                //的某些值符合選取的項目
                query.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                //
                NestedQueryBuilder attrs = QueryBuilders.nestedQuery("attrs", query, ScoreMode.None);

                boolQueryBuilder.filter(attrs);

                result.getUserdAttrs().add(Long.parseLong(attrId));

                SearchRespVO.FactorNavVO factorNavVO = new SearchRespVO.FactorNavVO();
                factorNavVO.setName(attrId);
                //用ID找名稱 體積:100
                R r =  productService.attrInfo(attrId);
                if(r.getCode() == 0){
                    AttrTO attrTO = r.getValue("attr", new TypeReference<AttrTO>() {
                    });
                    factorNavVO.setName(attrTO.getAttrName());
                }
                factorNavVO.setValue(Arrays.toString(attrValues));
                String replace = queryFullStringReplace(param.getQueryFullString(), "attrs", attr);
                String fullUrl = getQueryFullString(replace);
                factorNavVO.setUrl(fullUrl);
                result.getNavVOS().add(factorNavVO);
            }
        }
        dslBuilder.query(boolQueryBuilder);

        //sort
        if(!StringUtils.isEmpty(param.getSort())){
            String sort = param.getSort();
            String[] s = sort.split("_");
            dslBuilder.sort(s[0],s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
        }
        //一頁16條，第一頁就是0-15  16 * (1 - 1) = 0
        //從0開始15個
        dslBuilder.from(ElasticConstant.PRODUCT_PAGESIZE * (param.getPage() - 1));
        dslBuilder.size(ElasticConstant.PRODUCT_PAGESIZE);
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            dslBuilder.highlighter(highlightBuilder);
        }
        //聚合
        //用桶來看查詢結果中最相關的attr
        //品牌
        TermsAggregationBuilder brandIdAgg = AggregationBuilders.terms("brandId_agg");
        brandIdAgg.field("brandId").size(50);

        TermsAggregationBuilder brandName_agg = AggregationBuilders.terms("brandName_agg");
        brandName_agg.field("brandName").size(1);
        brandIdAgg.subAggregation(brandName_agg);

        TermsAggregationBuilder brandImg_agg = AggregationBuilders.terms("brandImg_agg");
        brandImg_agg.field("brandImg").size(1);
        brandIdAgg.subAggregation(brandImg_agg);

        dslBuilder.aggregation(brandIdAgg);
        //分類
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(50);

        TermsAggregationBuilder catalogName_agg = AggregationBuilders.terms("catalogName_agg");
        catalogName_agg.field("catalogName").size(1);
        catalog_agg.subAggregation(catalogName_agg);

        dslBuilder.aggregation(catalog_agg);
        //屬性聚合
        //先用ID分類，之後再顯示ID的名稱及值
        NestedAggregationBuilder attrsNested = AggregationBuilders.nested("attr_agg", "attrs");

        TermsAggregationBuilder attrId_agg = AggregationBuilders.terms("attrId_agg");
        attrId_agg.field("attrs.attrId");

        TermsAggregationBuilder attrName_agg = AggregationBuilders.terms("attrName_agg");
        attrName_agg.field("attrs.attrName").size(1);
        attrId_agg.subAggregation(attrName_agg);

        TermsAggregationBuilder attrValue_agg = AggregationBuilders.terms("attrValue_agg");
        attrValue_agg.field("attrs.attrValue").size(50);
        attrId_agg.subAggregation(attrValue_agg);

        attrsNested.subAggregation(attrId_agg);
        dslBuilder.aggregation(attrsNested);
        //
        System.out.println(dslBuilder.toString());

        SearchRequest req = new SearchRequest(new String[]{ ElasticConstant.SKUPRODUCT_INDEX},dslBuilder);

        return req;
    }
    private RangeQueryBuilder range(String column,String range){
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(column);
        if(range.startsWith("_")){
            //_500
            rangeQuery.lte(range.split("_")[1]);
        }
        else if(range.endsWith("_")){
            //500_
            rangeQuery.gte(range.split("_")[0]);
        }else {
            //500_500
            String[] s = range.split("_");
            rangeQuery.lte(s[1]);
            rangeQuery.gte(s[0]);
        }
        return rangeQuery;
    }
    private SearchRespVO handleResp(SearchResponse response, SearchParamVO param, SearchRespVO resp) {
//        resp.setAttrs();
        ParsedNested parsedNested = response.getAggregations().get("attr_agg");
        List<SearchRespVO.AttrVO> attrVOS = ((ParsedLongTerms) (parsedNested.getAggregations().get("attrId_agg"))).getBuckets().stream().map(item -> {
            SearchRespVO.AttrVO attrVO = new SearchRespVO.AttrVO();
            attrVO.setAttrID((Long) item.getKey());
            Object attrName = ((ParsedStringTerms) item.getAggregations().get("attrName_agg")).getBuckets().get(0).getKey();
            attrVO.setAttrName((String) attrName);
            List<String> attrValue = ((ParsedStringTerms) item.getAggregations().get("attrValue_agg")).getBuckets().stream().map(item2 -> {
                String value = (String) item2.getKey();
                return value;
            }).collect(Collectors.toList());
            attrVO.setAttrValue(attrValue);

            return attrVO;
        }).collect(Collectors.toList());
        resp.setAttrs(attrVOS);
//        resp.setCategoryVOS();
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        List<SearchRespVO.CategoryVO> categoryVOS = catalogAgg.getBuckets().stream().map(item -> {
            SearchRespVO.CategoryVO categoryVO = new SearchRespVO.CategoryVO();
            categoryVO.setCatalogId((Long) item.getKey());
            String catalogName = (String) ((ParsedStringTerms) item.getAggregations().get("catalogName_agg")).getBuckets().get(0).getKey();
            categoryVO.setCatalogName(catalogName);
            return categoryVO;
        }).collect(Collectors.toList());
        resp.setCategoryVOS(categoryVOS);
//        resp.setBrandVOS();
        ParsedLongTerms brandIdAgg = response.getAggregations().get("brandId_agg");
        List<SearchRespVO.BrandVO> brandVOS = brandIdAgg.getBuckets().stream().map(item -> {
            SearchRespVO.BrandVO brandVO = new SearchRespVO.BrandVO();
            brandVO.setBrandId((Long) item.getKey());

            List<? extends Terms.Bucket> brandImg_agg = ((ParsedStringTerms) (item.getAggregations().get("brandImg_agg"))).getBuckets();
            if(brandImg_agg.size() > 0){
                String brandImg = (String) brandImg_agg.get(0).getKey();
                brandVO.setBrandImg(brandImg);
            }

            String brandName = (String) ((ParsedStringTerms) (item.getAggregations().get("brandName_agg"))).getBuckets().get(0).getKey();
            brandVO.setBrandName(brandName);

            return brandVO;
        }).collect(Collectors.toList());
        resp.setBrandVOS(brandVOS);
//        resp.setModels();
        List<SkuEsModel> skuEsModels = Arrays.stream(response.getHits().getHits()).map(item -> {
            SkuEsModel sku = JSON.parseObject(item.getSourceAsString(), SkuEsModel.class);
            if(!StringUtils.isEmpty(param.getKeyword())){
                HighlightField skuTitleField = item.getHighlightFields().get("skuTitle");
                String highlightText = skuTitleField.fragments()[0].toString();
                sku.setSkuTitle(highlightText);
            }
            return sku;
        }).collect(Collectors.toList());
        resp.setModels(skuEsModels);
//        resp.setCurrentPage();
        resp.setCurrentPage(param.getPage());
//        resp.setTotalPage();
        Long total = response.getHits().getTotalHits().value;
        Long totalPage = total % ElasticConstant.PRODUCT_PAGESIZE != 0 ? total / ElasticConstant.PRODUCT_PAGESIZE + 1 : total / ElasticConstant.PRODUCT_PAGESIZE;
        resp.setTotalPage(totalPage);
//        resp.setTotalRow();
        resp.setTotalRow(total);
//        resp.setPages();
        List<Integer> pages = new ArrayList<>();
        for (Integer i = 1; i <= totalPage; i++) {
            pages.add(i);
        }
        resp.setPages(pages);
        return resp;
    }
    private String queryFullStringReplace(String queryFullString, String field, String value){
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+","%20");
            if(isFirstParam(queryFullString,field,encode)){
                queryFullString = queryFullString.replace(field +"="+encode,"");
            }else {
                queryFullString = queryFullString.replace("&" +field +"="+encode,"");
            }
            return queryFullString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    private boolean isFirstParam(String queryFullString, String field, String value){
        return queryFullString.indexOf(field+ "=" +value) - 1 == -1;
    }
    //應該是不會出現為空的狀態，因為之後都會是先選分類才會有各種Attr brand
    //attr=100&abc=400 -> attr=100 or &abc=400 or ""
    private String getQueryFullString(String url){
        if("".equals(url))return "http://search.gulimall.com/search.html";
        if(url.indexOf("&") == 0){
            url = url.substring(1);
            url = "http://search.gulimall.com/search.html?"+url;
            return url;
        }else {
            url = "http://search.gulimall.com/search.html?"+url;
            return url;
        }
    }
}
