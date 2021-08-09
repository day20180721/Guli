import com.alibaba.fastjson.JSON;
import com.littlejenny.gulimall.GuliSearch13000Main;
import com.littlejenny.gulimall.config.GulimallElasticSearchConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallElasticSearchConfig.class)
public class test {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Test
    public void loadRestHighLevelClient(){
        System.out.println(restHighLevelClient);
    }

    @Test
    public void index(){
        IndexRequest indexRequest = new IndexRequest("user");
        indexRequest.id("1");
        User user = new User("大王",20,"台灣","男");
        String userJSON = JSON.toJSONString(user);
        indexRequest.source(userJSON, XContentType.JSON);
        try {
            IndexResponse index = restHighLevelClient.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
            System.out.println(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void search(){
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("newcustomer");
        //查詢條件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("name","Xiao"));
        //
        TermsAggregationBuilder nameterms = AggregationBuilders.terms("nameterms").field("name.keyword").size(5);
        searchSourceBuilder.aggregation(nameterms);
        //
        AvgAggregationBuilder ageavg = AggregationBuilders.avg("ageavg").field("age");
        searchSourceBuilder.aggregation(ageavg);
        //
//        System.out.println(searchSourceBuilder);
        SearchRequest request = searchRequest.source(searchSourceBuilder);
        //
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(request, GulimallElasticSearchConfig.COMMON_OPTIONS);

//            for (SearchHit hit : response.getHits().getHits()) {
//                String sourceAsString = hit.getSourceAsString();
//                User user = JSON.parseObject(sourceAsString, User.class);
//                System.out.println(user);
//            }
            Aggregations aggregations = response.getAggregations();
            Terms aggregation = aggregations.get("nameterms");
            for (Terms.Bucket bucket : aggregation.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                System.out.println(keyAsString);
            }
            Avg ageavgA = aggregations.get("ageavg");
            System.out.println(ageavgA.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}