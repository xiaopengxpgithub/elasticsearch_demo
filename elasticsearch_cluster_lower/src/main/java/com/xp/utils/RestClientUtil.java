package com.xp.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientUtil.class);
    private static volatile RestClient restClient = null;

    private RestClientUtil() {
    }

    public static RestClient getRestClient() {
        if (restClient == null) {
            synchronized (RestClient.class) {
                if (restClient == null) {
                    LOGGER.info(Thread.currentThread().getName() + "初始化restClient...");

                    RestClientBuilder restClientBuilder = RestClient.builder(
                            new HttpHost("192.168.1.121", 9200),
                            new HttpHost("192.168.1.122", 9200),
                            new HttpHost("192.168.1.123", 9200));

                    restClientBuilder.setFailureListener(new RestClient.FailureListener() {
                        @Override
                        public void onFailure(Node node) {
                            LOGGER.info("节点:" + node.toString() + ",链接失败...");
                        }
                    });

                    restClient = restClientBuilder.build();
                }
            }
        }

        return restClient;
    }

    public static void releaseRestClient() {
        if (restClient != null) {
            try {
                //关闭链接,但是RestClient实例还在,后续的线程可以接着使用
                restClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i < 10; i++) {
            new Thread(() -> {
                RestClient restClient = RestClientUtil.getRestClient();
                System.out.println(Thread.currentThread().getName() + restClient);

                RestClientUtil.releaseRestClient();

            }, "线程" + i).start();
        }
    }
}
