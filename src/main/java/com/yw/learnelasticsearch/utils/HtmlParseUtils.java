package com.yw.learnelasticsearch.utils;

import com.yw.learnelasticsearch.entity.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: learn-elasticsearch
 * @description:
 * @author: YW
 * @create: 2020-09-04 12:06
 **/
public class HtmlParseUtils {

    public static List<Content> parseJd(String keyWord) throws IOException {
        //获取请求
        String url = "https://search.jd.com/Search?keyword=" + keyWord;
        //获取页面
        final Document document = Jsoup.parse(new URL(url), 30000);
        //获取元素
        final Element j_goodsList = document.getElementById("J_goodsList");
        //获取所有标签
        final Elements li = j_goodsList.getElementsByTag("li");
        final ArrayList<Content> results = new ArrayList<>();
        for (Element element : li) {
            final String img = element.getElementsByTag("img").eq(0).attr("src");
            final String price = element.getElementsByClass("p-price").eq(0).text();
            final String title = element.getElementsByClass("p-name").eq(0).text();
            results.add(new Content(title, img, price));
        }
        return results;
    }
}
