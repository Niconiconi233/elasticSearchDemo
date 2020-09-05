package com.yw.learnelasticsearch.controller;

import com.yw.learnelasticsearch.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @program: learn-elasticsearch
 * @description:
 * @author: YW
 * @create: 2020-09-04 14:52
 **/
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping("/content")
    public Boolean content(@RequestParam String keyword){
        return contentService.parseContent(keyword);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable("keyword") String keyword, @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) throws IOException {
        return contentService.search(keyword, pageNo, pageSize);
    }

}
