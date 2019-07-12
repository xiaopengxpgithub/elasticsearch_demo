package com.xp.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RestHighClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestHighClientUtil.class);
    private static volatile RestHighLevelClient restHighLevelClient = null;

    private RestHighClientUtil() {
    }

    public static RestHighLevelClient getRestHighLevelClient() {
        if (restHighLevelClient == null) {
            synchronized (RestHighLevelClient.class) {
                if (restHighLevelClient == null) {

                    LOGGER.info(Thread.currentThread().getName() + "初始化...");

                    RestClientBuilder restClientBuilder = RestClient.builder(
                            new HttpHost("192.168.1.121", 9200),
                            new HttpHost("192.168.1.122", 9200),
                            new HttpHost("192.168.1.123", 9200));

                    restHighLevelClient = new RestHighLevelClient(restClientBuilder);
                }
            }
        }

        return restHighLevelClient;
    }
}
