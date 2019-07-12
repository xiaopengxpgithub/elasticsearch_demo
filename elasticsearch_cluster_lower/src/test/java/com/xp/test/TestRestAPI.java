package com.xp.test;

import com.xp.utils.Constant;
import com.xp.utils.JsonUtils;
import com.xp.utils.RestClientUtil;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestRestAPI {

    private RestClient restClient = RestClientUtil.getRestClient();

    @Test
    public void testSearch() throws IOException {
        //构建请求
        String _id = "ac6G5WsBPUXepTyEkSDY";
        Request request = new Request(Constant.REQUEST_POST, Constant.BASE_URI + Constant.INDEX_HAOKE + Constant.BASE_URI + Constant.INDEX_HAOKE_TYPE_HOUSE+Constant.BASE_URI+Constant.ENDPOINT_SEARCHER);
        request.addParameter("pretty", "true"); //格式美化

        //请求参数
        String json_param = "{\"query\":{\"match\":{\"title\":\"拎包入住\"}}}";
        request.setJsonEntity(json_param);

        //发送请求
        Response response = restClient.performRequest(request);

        printResponse(response);

        RestClientUtil.releaseRestClient();
    }

    @Test
    public void testQuery() throws IOException {
        //构建请求
        String _id = "ac6G5WsBPUXepTyEkSDY";
        Request request = new Request(Constant.REQUEST_GET, Constant.BASE_URI + Constant.INDEX_HAOKE + Constant.BASE_URI + Constant.INDEX_HAOKE_TYPE_HOUSE + Constant.BASE_URI + _id);
        request.addParameter("pretty", "true"); //格式美化

        //发送请求
        Response response = restClient.performRequest(request);

        printResponse(response);

        RestClientUtil.releaseRestClient();
    }

    @Test
    public void testSave() throws IOException {
        //构建请求
        Request request = new Request(Constant.REQUEST_POST, Constant.BASE_URI + Constant.INDEX_HAOKE + Constant.BASE_URI + Constant.INDEX_HAOKE_TYPE_HOUSE);
        request.addParameter("pretty", "true"); //格式美化

        //构造json数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", "1007");
        map.put("title", "南京西路一室一厅 2000");
        map.put("price", "2000");

        String json_str = JsonUtils.objectToJson(map);
        request.setJsonEntity(json_str);

        //发送请求
        Response response = restClient.performRequest(request);

        printResponse(response);

        RestClientUtil.releaseRestClient();
    }


    @Test
    public void testInfo() throws IOException {
        //查询集群信息
        Request request = new Request("GET", "/_cluster/state");
        request.addParameter("pretty", "true"); //格式美化
        Response response = restClient.performRequest(request);

        printResponse(response);

        RestClientUtil.releaseRestClient();
    }

    public void printResponse(Response response) throws IOException {
        System.out.println("请求->" + response.getRequestLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}
