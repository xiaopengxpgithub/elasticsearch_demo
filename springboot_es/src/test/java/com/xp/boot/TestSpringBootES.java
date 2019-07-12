package com.xp.boot;

import com.xp.boot.pojo.User;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSpringBootES {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Test
    public void search(){
        PageRequest pageRequest=PageRequest.of(1,10);//分页,查询第一页,10条数据
        SearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("userName","张三"))
                .withPageable(pageRequest)
                .build();

        AggregatedPage<User> users =elasticsearchTemplate.queryForPage(searchQuery,User.class);

        System.out.println("总页数:"+users.getTotalPages());
        for (User user : users) {
            System.out.println(user.toString());
        }
    }

    @Test
    public void delete(){
        elasticsearchTemplate.delete(User.class,"6000");
    }

    @Test
    public void update() {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("age",30);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(map);

        UpdateQuery updateQuery=new UpdateQueryBuilder()
                .withId("1001")
                .withClass(User.class)
                .withIndexRequest(indexRequest).build();

        UpdateResponse updateResponse =elasticsearchTemplate.update(updateQuery);
        System.out.println(updateResponse.getResult());  //UPDATED
    }

    @Test
    public void bulk() {
        List<IndexQuery> list = new ArrayList<IndexQuery>();
        for (int i = 1; i < 5000; i++) {
            User user = new User();
            user.setId(1001 + i);
            user.setUserName("张三" + i);
            user.setAge(25);
            user.setHobby("足球,音乐,跑步");

            IndexQuery indexQuery = new IndexQueryBuilder().withObject(user).build();
            list.add(indexQuery);
        }

        long start = System.currentTimeMillis();
        elasticsearchTemplate.bulkIndex(list);
        System.out.println("用时:" + (System.currentTimeMillis() - start));  //13865ms
    }

    @Test
    public void save() {
        User user = new User();
        user.setId(1001);
        user.setUserName("张三");
        user.setAge(25);
        user.setHobby("篮球,游泳,音乐");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(user).build();

        String result = elasticsearchTemplate.index(indexQuery);

        System.out.println(result); //1001
    }
}
