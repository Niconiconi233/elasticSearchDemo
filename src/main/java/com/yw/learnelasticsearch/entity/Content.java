package com.yw.learnelasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @program: learn-elasticsearch
 * @description:
 * @author: YW
 * @create: 2020-09-04 14:18
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content implements Serializable {
    private static final long serialVersionUID = -4356459678522179239L;
    private String title;
    private String img;
    private String price;
}
