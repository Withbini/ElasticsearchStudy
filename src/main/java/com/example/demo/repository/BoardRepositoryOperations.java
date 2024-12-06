package com.example.demo.repository;

import com.example.demo.entity.Board;
import com.example.demo.entity.SearchType;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class BoardRepositoryOperations {
    private final ElasticsearchOperations operations;

    public SearchHits<Board> search(String keyword, SearchType searchType, String sortField, String sortOrder, Pageable pageable) {
        String fieldToSort = (sortField == null || sortField.isEmpty()) ? "generatedAt" : sortField;
        String orderToSort = (sortOrder == null || sortOrder.isEmpty()) ? "desc" : sortOrder;

        Query query = new NativeSearchQueryBuilder()
                .withQuery(getSearchQuery(keyword, searchType))
                .withSort(SortBuilders.fieldSort(fieldToSort)
                        .order("asc".equalsIgnoreCase(orderToSort) ? SortOrder.ASC : SortOrder.DESC))
                .withPageable(pageable)
                .build();
        return operations.search(query, Board.class);
    }

    public SearchHits<Board> getTotalData(String field) {
        // 집계 빌더 생성
        SumAggregationBuilder sumAggregation = AggregationBuilders.sum("total_" + field).field(field);

        // 쿼리 작성
        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())  // 모든 문서 대상
                .addAggregation(sumAggregation)           // 집계 추가
                .build();

        // 집계 결과 조회
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
