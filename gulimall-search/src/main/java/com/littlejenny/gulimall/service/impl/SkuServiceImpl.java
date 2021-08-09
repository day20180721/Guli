package com.littlejenny.gulimall.service.impl;

import com.alibaba.fastjson.JSON;
import com.littlejenny.common.to.es.SkuEsModel;
import com.littlejenny.gulimall.config.GulimallElasticSearchConfig;
import com.littlejenny.gulimall.constant.ElasticConstant;
import com.littlejenny.gulimall.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    RestHighLevelClient client;
    @Override
    public boolean saveBatch(List<SkuEsModel> models) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel model : models) {
            IndexRequest indexRequest = new IndexRequest(ElasticConstant.SKUPRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            String s = JSON.toJSONString(model);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse response = client.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        boolean b = response.hasFailures();
        List<String> collect = Arrays.stream(response.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        if(b){
            log.error("elastic 儲存錯誤 : {}",collect);
        }else {
            log.info("elastic 儲存成功 :{}",collect);
        }
        return !b;
    }
}
