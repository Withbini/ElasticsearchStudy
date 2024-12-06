package com.example.demo.util;

import com.example.demo.dto.SearchResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchResultMapper {
    public static <Entity, Dto> SearchResult<Dto> mapToSearchResult(SearchHits<Entity> searchHits, Pageable pageable, Function<Entity, Dto> mapper) {
        List<Dto> content = searchHits.getSearchHits().stream()
                .map(hit -> mapper.apply(hit.getContent()))
                .collect(Collectors.toList());

        long totalElements = searchHits.getTotalHits();

        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        int currentPage = pageable.getPageNumber();
        boolean isLast = currentPage + 1 >= totalPages;

        return new SearchResult<>(content, new SearchResult.Meta(currentPage + 1, totalPages, isLast));
    }
}
