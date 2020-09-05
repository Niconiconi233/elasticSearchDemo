package com.yw.learnelasticsearch.config.ES;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: learn-elasticsearch
 * @description:
 * @author: YW
 * @create: 2020-09-03 16:51
 **/
@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.nodes}")
    private List<String> nodes;

    @Value("${elasticsearch.schema}")
    private String schema;

    @Value("${elasticsearch.max-connect-total}")
    private Integer maxConnectTotal;

    @Value("${elasticsearch.max-connect-per-route}")
    private Integer maxConnectPerRoute;

    @Value("${elasticsearch.request-timeout-millis}")
    private Integer requestTimeoutMillis;

    @Value("${socket-timeout-millis}")
    private Integer socketTimeoutMillis;

    @Value("${connect-timeout-millis}")
    private Integer connectTimeoutMillis;

// 单机配置
//    @Bean
//    public RestHighLevelClient restHighLevelClient(){
//        RestHighLevelClient http = new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")));
//        return http;
//    }


    @Bean
    public RestHighLevelClient restHighLevelClient(){
        final ArrayList<HttpHost> httpHosts = new ArrayList<>();

        for (String node : nodes) {
            try {
                final String[] split = StringUtils.split(node, ":");
                Assert.notNull(split, "Must defied");
                Assert.state(split.length == 2, "Must be defined as 'host:port'");
                httpHosts.add(new HttpHost(split[0], Integer.valueOf(split[1]), schema));
            }catch (Exception e)
            {
                throw new IllegalStateException("Invaild ES nodes" + "property" + node + " " + e);
            }
        }
        return EsClientBuilder.build(httpHosts)
                .setConnectionRequestTimeoutMillis(requestTimeoutMillis)
                .setConnectTimeoutMillis(connectTimeoutMillis)
                .setSocketTimeoutMillis(socketTimeoutMillis)
                .setMaxConnectTotal(maxConnectTotal)
                .setMaxConnectPerRoute(maxConnectPerRoute)
                .create();
    }

}
