package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@AllArgsConstructor
@Builder
@Document(indexName = "jaebin", createIndex = false)
public class Board {
    @Id
    private String id;
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String author;
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String title;
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String contents;

    @Field(type = FieldType.Date)
    private String generatedAt;
    @Field(type = FieldType.Date)
    private String lastModified;

    @Field(type = FieldType.Integer)
    private Integer views;
    @Field(type = FieldType.Integer)
    private Integer thumbsUp;
}
