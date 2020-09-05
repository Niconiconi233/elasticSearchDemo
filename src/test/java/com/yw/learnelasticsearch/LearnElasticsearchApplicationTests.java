package com.yw.learnelasticsearch;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.security.user.privileges.ApplicationPrivilege;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

@SpringBootTest
class LearnElasticsearchApplicationTests {

    @Resource(name = "restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Test
    void test() {
        assert(restHighLevelClient != null);
    }

    //创建索引
    @Test
    void testCreateIndex() throws IOException {
        final CreateIndexRequest test_index = new CreateIndexRequest("test_index");
        test_index.settings("{\n" +
                "    \"analysis\":{   \n" +
                "      \"analyzer\":{\n" +
                "        \"ik\":{\n" +
                "          \"tokenizer\":\"ik_max_word\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }", XContentType.JSON);
        test_index.mapping(
                "{\n" +
                        "  \"properties\": {\n" +
                        "    \"message\": {\n" +
                        "      \"type\": \"text\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                XContentType.JSON);
        final CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(test_index, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    //获取索引
    @Test
    void testGetIndex() throws IOException {
        final GetIndexRequest test_index = new GetIndexRequest("test_index");
        final boolean exists = restHighLevelClient.indices().exists(test_index, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //删除索引
    @Test
    void testDeleteIndex() throws IOException {
        final DeleteIndexRequest test_index = new DeleteIndexRequest("test_index");
        final AcknowledgedResponse delete = restHighLevelClient.indices().delete(test_index, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    //index api
    //插入数据
    @Test
    void testAddDocument() throws IOException {
        final IndexRequest test_index = new IndexRequest("test_index");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        //test_index.source(jsonString, XContentType.JSON);
        test_index.source("user", "dr", "postDate", new Date(), "message", "trying fuck you");
        final IndexResponse index = restHighLevelClient.index(test_index, RequestOptions.DEFAULT);
        System.out.println(index.status());
    }

    //GET API
    //获取
    @Test
    void testGetDocument() throws IOException {
        final GetRequest getRequest = new GetRequest("test_index");
        getRequest.id("oM2hU3QB9xKA-4VfqzKO");
        final GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(documentFields.getSourceAsString());
    }

    //DELETE API
    //删除
    @Test
    void testDeleteDocument() throws IOException {
        final DeleteRequest test_index = new DeleteRequest("test_index");
        test_index.id("oM2hU3QB9xKA-4VfqzKO");
        final DeleteResponse delete = restHighLevelClient.delete(test_index, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }

    //UPDATE API
    //修改
    @Test
    void testUpdateDocument() throws IOException {
        final UpdateRequest test_index = new UpdateRequest("test_index", "n817U3QB9xKA-4VfszJM");
        final HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("updated", new Date());
        stringObjectHashMap.put("reason", "daily update");
        stringObjectHashMap.put("user", "dr");
        test_index.doc(stringObjectHashMap);
        final UpdateResponse update = restHighLevelClient.update(test_index, RequestOptions.DEFAULT);
        System.out.println(update.getResult());
    }

    //BULK API
    //批量操作 增删改
    @Test
    void testBulk() throws IOException {
        final BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("5s");
        bulkRequest.add(new IndexRequest("test_index").source("user", "dingrui1", "postDate", new Date(), "message", "bulk insert1"));
        bulkRequest.add(new IndexRequest("test_index").source("user", "dingrui2", "postDate", new Date(), "message", "bulk insert2"));
        bulkRequest.add(new IndexRequest("test_index").source("user", "dingrui3", "postDate", new Date(), "message", "bulk insert3"));
        bulkRequest.add(new IndexRequest("test_index").source("user", "dingrui4", "postDate", new Date(), "message", "bulk insert4"));
        final BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.status());
    }

    //UPDATE BY QUERY API
    //定向更新
    @Test
    void testUpdateByQuery() throws IOException {
        final UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest("test_index");
        updateByQueryRequest.setQuery(new MatchQueryBuilder("user", "dingrui"));
        updateByQueryRequest.setScript(new Script(ScriptType.INLINE, "painless", "if(cxt._source.postDate=='2020-09-03T12:54:45.197Z'){cxt._source.postDate='2020-09-03T13:54:44.928Z'}", Collections.emptyMap()));
        final BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
        System.out.println(bulkByScrollResponse.getStatus());
    }

    //DELETE BY QUERY API
    //定向删除
    @Test
    void testDeleteByQuery() throws IOException {
        final DeleteByQueryRequest test_index = new DeleteByQueryRequest("test_index");
        test_index.setQuery(new TermQueryBuilder("user", "dr"));
        final BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.deleteByQuery(test_index, RequestOptions.DEFAULT);
        System.out.println(bulkByScrollResponse.getStatus());
    }

    /*-------------------------------------------------------------------------------search----------------------------------------------------------------------------*/


    // SEARCH API
    //查询
    @Test
    void testSearch() throws IOException {
        final SearchRequest test_index = new SearchRequest("test_index");
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("user", "dingrui1"));
        //高亮
        final HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("user");
        highlightBuilder.preTags("<p class='clazz' style='color:red'>");
        highlightBuilder.postTags("</p>");
        searchSourceBuilder.highlighter(highlightBuilder);
        //排序
        //searchSourceBuilder.sort("postDate", SortOrder.ASC);
        searchSourceBuilder.sort(new FieldSortBuilder("postDate").order(SortOrder.ASC));
        //过滤器
        searchSourceBuilder.fetchSource(new String[]{"user", "message"}, new String[]{"postDate"});
        //分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
        test_index.source(searchSourceBuilder);
        final SearchResponse search = restHighLevelClient.search(test_index, RequestOptions.DEFAULT);
        final SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            System.out.println(hit.getHighlightFields());
        }
    }

}
