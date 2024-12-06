package com.example.demo.util;

import com.example.demo.dto.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class SearchResultMapper {
    public static <Entity, Dto> SearchResult<Dto> mapToSearchResult(SearchHits<Entity> searchHits, Pageable pageable, Function<Entity, Dto> mapper) {
        List<Dto> content = searchHits.getSearchHits().stream()
                .map(hit -> mapper.apply(hit.getContent()))
                .collect(Collectors.toList());

        long totalElements = searchHits.getTotalHits();

        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        int currentPage = pageable.getPageNumber();
        int size = pageable.getPageSize();
        boolean isLast = currentPage + 1 >= totalPages;
        log.info("JBJB totalElements : {} total page : {} currentPage:{} pageable.getPageSize() {} size :{}", totalElements, totalPages, currentPage, pageable.getPageSize(), size);

        return new SearchResult<>(content, new SearchResult.Meta(currentPage, totalPages, size, isLast));
    }
}
