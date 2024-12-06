package com.example.demo.repository;

import com.example.demo.entity.Board;
import com.example.demo.entity.SearchType;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryOperations {
    private final ElasticsearchOperations operations;

    public SearchHits<Board> search(String keyword, SearchType searchType, String sortField, String sortOrder, Pageable pageable) {
        String fieldToSort = (sortField == null || sortField.isEmpty()) ? "generatedAt" : sortField;
        String orderToSort = (sortOrder == null || sortOrder.isEmpty()) ? "desc" : sortOrder;

        Query query = new NativeSearchQueryBuilder()
                .withQuery(getSearchQuery(keyword, searchType))
                .withSort(fieldSort(fieldToSort)
                        .order("asc".equalsIgnoreCase(orderToSort) ? SortOrder.ASC : SortOrder.DESC))
                .withPageable(pageable)
                .build();
        return operations.search(query, Board.class);
    }

    private QueryBuilder getSearchQuery(String keyword, SearchType searchType) {
        if(keyword.isEmpty())
            return QueryBuilders.matchAllQuery();

        switch (searchType) {
            case AUTHOR:
                return QueryBuilders.matchQuery("author", keyword);
            case TITLE:
                return QueryBuilders.matchQuery("title", keyword);
            case CONTENT:
                return QueryBuilders.matchQuery("contents", keyword);
            case TITLE_AND_CONTENT:
                return QueryBuilders.multiMatchQuery(keyword, "title", "contents");
            default:
                throw new IllegalArgumentException("Unknown SearchType: " + searchType);
        }
    }
}
