package com.yw.learnelasticsearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @program: learn-elasticsearch
 * @description:
 * @author: YW
 * @create: 2020-09-04 11:51
 **/
@Controller
public class IndexController {

    @GetMapping("/index")
    public String index(){
        return "index";
    }
}
