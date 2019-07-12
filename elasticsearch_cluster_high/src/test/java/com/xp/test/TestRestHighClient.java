package com.xp.test;

import com.xp.utils.Constant;
import com.xp.utils.RestHighClientUtil;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestRestHighClient {

    private RestHighLevelClient restHighLevelClient = RestHighClientUtil.getRestHighLevelClient();


    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest(Constant.INDEX_HAOKE);
        searchRequest.types(Constant.INDEX_HAOKE_TYPE_HOUSE);

        //查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title","拎包入住"));
        //分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        System.out.println("搜索到:"+searchResponse.getHits().totalHits+"条数据");

        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println(hit.getSourceAsString());
        }
    }

    @Test
    public void testUpdate() throws IOException {
        String _id = "aM6G5WsBPUXepTyEkSDY";
        UpdateRequest updateRequest = new UpdateRequest(Constant.INDEX_HAOKE, Constant.INDEX_HAOKE_TYPE_HOUSE, _id);

        //数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "整租-南丹大厦 1居室 1800");
        map.put("price", "1800");

        updateRequest.doc(map);

        UpdateResponse response = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

        System.out.println(response.getResult());

        restHighLevelClient.close();
    }

    @Test
    public void testDelete() throws IOException {
        String _id = "aYTZ5WsBq0w6YpwTZNDO";
        DeleteRequest deleteRequest = new DeleteRequest(Constant.INDEX_HAOKE, Constant.INDEX_HAOKE_TYPE_HOUSE, _id);

        DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response.getResult());  //如果数据不存在则返回"NOT FOUND"

        restHighLevelClient.close();
    }

    @Test
    public void testExists() throws IOException {
        //构建请求
        String _id = "ac6G5WsBPUXepTyEkSDY";
        GetRequest getRequest = new GetRequest(Constant.INDEX_HAOKE, Constant.INDEX_HAOKE_TYPE_HOUSE, _id);

        //发送请求
        boolean result = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println("数据是否存在:" + result);

        restHighLevelClient.close();
    }

    @Test
    public void testQuery() throws IOException {
        //构建请求
        String _id = "ac6G5WsBPUXepTyEkSDY";
        GetRequest getRequest = new GetRequest(Constant.INDEX_HAOKE, Constant.INDEX_HAOKE_TYPE_HOUSE, _id);

        //指定返回的字段
        String[] responseIncludes = new String[]{"title", "price"};
        String[] responseExcludes = Strings.EMPTY_ARRAY;

        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, responseIncludes, responseExcludes);
        getRequest.fetchSourceContext(fetchSourceContext);

        //发送请求
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("查询结果:" + response.getSourceAsString());

        restHighLevelClient.close();
    }

    @Test
    public void testSaveAsync() throws IOException, InterruptedException {
        //构建请求
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", "1010");
        map.put("title", "南京南路一室一厅 2400");
        map.put("price", "2400");
        IndexRequest indexRequest = new IndexRequest(Constant.INDEX_HAOKE, Constant.INDEX_HAOKE_TYPE_HOUSE)
                .source(map);

        //发送请求,异步执行
        restHighLevelClient.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                System.out.println("result:" + indexResponse.getResult());
                System.out.println("shardInfo:" + indexResponse.getShardInfo());
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("请求失败..." + e);
            }
        });

        //这里必须要等待,保证程序不会关闭,否则无法执行异步任务
        Thread.sleep(2000);
    }

    @Test
    public void testSave() throws IOException {
        //构建请求
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", "1009");
        map.put("title", "南京北路一室一厅 2600");
        map.put("price", "3000");
        IndexRequest indexRequest = new IndexRequest(Constant.INDEX_HAOKE, Constant.INDEX_HAOKE_TYPE_HOUSE)
                .source(map);

        //发送请求
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(response.getResult());
        System.out.println(response.getShardInfo());

        restHighLevelClient.close();
    }
}
