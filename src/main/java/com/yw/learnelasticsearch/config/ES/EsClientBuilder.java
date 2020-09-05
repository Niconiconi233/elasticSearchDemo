package com.yw.learnelasticsearch.config.ES;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.List;

/**
 * @program: learn-elasticsearch
 * @description:
 * @author: YW
 * @create: 2020-09-04 22:48
 **/
public class EsClientBuilder {
    private int connectTimeoutMillis = 1000;
    private int socketTimeoutMillis = 30000;
    private int connectionRequestTimeoutMillis = 500;
    private int maxConnectPerRoute = 10;
    private int maxConnectTotal = 30;

    private final List<HttpHost> httpHosts;

    private EsClientBuilder(List<HttpHost> httpHosts){
        this.httpHosts = httpHosts;
    }

    public EsClientBuilder setConnectionRequestTimeoutMillis(int connectionRequestTimeoutMillis) {
        this.connectionRequestTimeoutMillis = connectionRequestTimeoutMillis;
        return this;
    }

    public EsClientBuilder setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    public EsClientBuilder setMaxConnectPerRoute(int maxConnectPerRoute) {
        this.maxConnectPerRoute = maxConnectPerRoute;
        return this;
    }

    public EsClientBuilder setMaxConnectTotal(int maxConnectTotal) {
        this.maxConnectTotal = maxConnectTotal;
        return this;
    }

    public EsClientBuilder setSocketTimeoutMillis(int socketTimeoutMillis) {
        this.socketTimeoutMillis = socketTimeoutMillis;
        return this;
    }

    public static EsClientBuilder build(List<HttpHost> httpHosts){
        return new EsClientBuilder(httpHosts);
    }

    public RestHighLevelClient create(){
        final HttpHost[] httpHosts = this.httpHosts.toArray(new HttpHost[0]);
        final RestClientBuilder builder = RestClient.builder(httpHosts);

        builder.setRequestConfigCallback(requestConfigBuilder->{
            requestConfigBuilder.setConnectTimeout(connectTimeoutMillis);
            requestConfigBuilder.setSocketTimeout(socketTimeoutMillis);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeoutMillis);
            return requestConfigBuilder;
        });

        builder.setHttpClientConfigCallback(httpClientBuilder->{
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            httpClientBuilder.setMaxConnTotal(maxConnectTotal);
            return httpClientBuilder;
        });

        return new RestHighLevelClient(builder);
    }
}
