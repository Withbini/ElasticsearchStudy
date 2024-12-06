package com.example.demo.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class BoardDataDto {
    private final String id;
    private final String title;
    private final String author;

    private final String generatedAt;
    private final String lastModified;

    private final Integer views;
    private final Integer thumbsUp;
}