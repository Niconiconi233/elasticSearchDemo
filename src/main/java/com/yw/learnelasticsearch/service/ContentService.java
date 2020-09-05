package com.yw.learnelasticsearch.service;

/**
 * @program: learn-elasticsearch
 * @description:
 * @author: YW
 * @create: 2020-09-04 14:33
 **/

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yw.learnelasticsearch.entity.Content;
import com.yw.learnelasticsearch.utils.HtmlParseUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    public Boolean parseContent(String keyWord){
        try{
            final List<Content> contents = HtmlParseUtils.parseJd(keyWord);
            final BulkRequest bulkRequest = new BulkRequest();
            final ObjectMapper objectMapper = new ObjectMapper();
            for (Content content : contents) {
                bulkRequest.add(new IndexRequest("jd_index").source(objectMapper.writeValueAsString(content), XContentType.JSON));
            }
            bulkRequest.timeout("30s");
            final BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !bulk.hasFailures();
        }catch (Exception e){
            return false;
        }
    }

    public List<Map<String, Object>> search(String keyword, int page, int size) throws IOException {
        if(page <= 1){
            page = 0;
        }

        final SearchRequest jd_index = new SearchRequest("jd_index");
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(page);
        searchSourceBuilder.size(size);
        searchSourceBuilder.query(new MatchQueryBuilder("title", keyword));
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.SECONDS));

        //高亮
        final HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        searchSourceBuilder.highlighter(highlightBuilder);
        jd_index.source(searchSourceBuilder);
        final SearchResponse search = restHighLevelClient.search(jd_index, RequestOptions.DEFAULT);

        final SearchHit[] hits = search.getHits().getHits();
        final ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (SearchHit hit : hits) {
            final Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            final HighlightField title = highlightFields.get("title");
            final Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if(!StringUtils.isEmpty(title)){
                final Text[] fragments = title.fragments();
                String n_title = "";
                for (Text fragment : fragments) {
                    n_title += fragment;
                }
                sourceAsMap.put("title", n_title);
            }
            maps.add(sourceAsMap);
        }
        return maps;
    }
}
