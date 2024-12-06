package com.example.demo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class SearchResult<T> {
    public SearchResult(List<T> data, Meta meta) {
        this.data = data;
        this.meta = meta;
    }

    private final List<T> data;
    private final Meta meta;

    @Getter
    @RequiredArgsConstructor
    public static class Meta {
        private final int page;
        private final int total;
        private final boolean isLast;
    }
}
