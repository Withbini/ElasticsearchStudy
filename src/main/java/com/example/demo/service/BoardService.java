package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.ActionType;
import com.example.demo.entity.Board;
import com.example.demo.entity.SearchType;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.BoardRepositoryOperations;
import com.example.demo.util.SearchResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final ElasticsearchOperations operations;
    private final BoardRepositoryOperations boardRepositoryOperations;

    public GetMultipleBoardResponseDto searchByTitleAndContents(String author, String title, String contents, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        if (!author.isEmpty()) {
            var li = boardRepository.findByAuthorContainingOrderByGeneratedAtDesc(author, pageable);
            return GetMultipleBoardResponseDto.fromEntity(li);
        } else if (title.isEmpty() && !contents.isEmpty()) {
            var li = boardRepository.findByContentsContainingOrderByGeneratedAtDesc(contents, pageable);
            return GetMultipleBoardResponseDto.fromEntity(li);
        } else if (!contents.isEmpty()) {
            var li = boardRepository.findByTitleOrContentsOrderByGeneratedAtDesc(title, contents, pageable);
            return GetMultipleBoardResponseDto.fromEntity(li);
        } else if (!title.isEmpty()) {
            var li = boardRepository.findByTitleContainingOrderByGeneratedAtDesc(title, pageable);
            return GetMultipleBoardResponseDto.fromEntity(li);
        } else {
            var li = boardRepository.findAllByOrderByGeneratedAtDesc(pageable);
            return GetMultipleBoardResponseDto.fromEntity(li);
        }
    }

    public SearchResult<BoardDataDto> search(String keyword, SearchType searchType, String sortField, String sortOrder, Pageable pageable) {
        var result = boardRepositoryOperations.search(keyword, searchType, sortField, sortOrder, pageable);
        return SearchResultMapper.mapToSearchResult(result, pageable,
                entity -> new BoardDataDto(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getAuthor(),
                        entity.getGeneratedAt(),
                        entity.getLastModified(),
                        entity.getViews(),
                        entity.getThumbsUp()));
    }

    public GetBoardResponseDto searchById(String id) {
        var board = boardRepository.findById(id);
        if (board.isPresent()) {
            Map<String, Object> updateMap = new HashMap<>();
            addNeedUpdate("views", board.get().getViews() + 1, updateMap);
            updateQuery(id, updateMap);
            return GetBoardResponseDto.fromEntity(board.get());
        } else
            return GetBoardResponseDto.builder().build();
    }

    private String updateQuery(String id, Map<String, Object> updateMap) {
        UpdateQuery updateQuery = UpdateQuery.builder(id)
                .withDocument(Document.from(updateMap))
                .build();
        IndexCoordinates indexCoordinates = IndexCoordinates.of("jaebin");
        var response = operations.update(updateQuery, indexCoordinates);
        return response.getResult().toString();
    }

    private static <T> void addNeedUpdate(String field, T newValue, Map<String, Object> updateMap) {
        updateMap.put(field, newValue);
    }

    public GetBoardResponseDto save(CreateRequestDto requestDto) {
        Board board = Board.builder()
                .id(UUID.randomUUID().toString())
                .title(requestDto.getTitle())
                .author(requestDto.getAuthor())
                .contents(requestDto.getContents())
                .generatedAt(LocalDateTime.now().toString())
                .lastModified(LocalDateTime.now().toString())
                .thumbsUp(0)
                .views(0)
                .build();
        boardRepository.save(board);
        return GetBoardResponseDto.fromEntity(board);
    }

    public void deleteById(String id) {
        boardRepository.deleteById(id);
    }

    public String update(BoardUpdateRequestDto requestDto) {
        String id = requestDto.getId();
        String newTitle = requestDto.getTitle();
        String newContents = requestDto.getContents();

        Map<String, Object> updateMap = new HashMap<>();
        if (!newTitle.isEmpty()) {
            addNeedUpdate("title", newTitle, updateMap);
        }
        if (!newContents.isEmpty()) {
            addNeedUpdate("contents", newContents, updateMap);
        }
        if (!updateMap.isEmpty()) {
            var board = boardRepository.findById(id);
            if (board.isPresent()) {
                return updateQuery(id, updateMap);
            }
        }
        return "";
    }

    public BoardDataDto updateData(BoardUpdateDataRequestDto requestDto) {
        final String id = requestDto.getId();
        ActionType actionType = requestDto.getAction();
        String target = requestDto.getTarget();
        String value = requestDto.getValue();

        var board = boardRepository.findById(id);
        if (board.isEmpty()) {
            return BoardDataDto.empty();
        }

        Map<String, Object> updateMap = new HashMap<>();
        if (actionType == ActionType.INCREASE) {
            increase(board.get(), target, updateMap);
        } else if (actionType == ActionType.DECREASE) {
            decrease(board.get(), target, updateMap);
        } else if (actionType == ActionType.SET) {
            changeValue(board.get(), target, value, updateMap);
        }

        if (!updateMap.isEmpty()) {
            updateQuery(id, updateMap);
            return BoardDataDto.fromEntity(board.get());
        }
        return BoardDataDto.empty();
    }


    public Long getTotalSummary(String target) {
        if (target.equals("views"))
            return getTotalViews();
        else if (target.equals("thumbsUp"))
            return getTotalThumbsUp();
        else return 0L;
    }

    private Long getTotalViews() {
        Sum sum = boardRepositoryOperations.getTotalData("views").getAggregations().get("total_views");
        return Math.round(sum.value());
    }

    private Long getTotalThumbsUp() {
        Sum sum = boardRepositoryOperations.getTotalData("thumbsUp").getAggregations().get("total_thumbsUp");
        return Math.round(sum.value());
    }


    private void changeValue(Board board, String target, String value, Map<String, Object> updateMap) {
        Field field = null;
        try {
            field = board.getClass().getDeclaredField(target);
            field.setAccessible(true); // private 필드 접근 허용
            Class<?> fieldType = field.getType();
            if (fieldType == String.class) {
                addNeedUpdate(target, value, updateMap);
            } else {
                throw new RuntimeException("integer 타입이 아닙니다");
            }

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private void increase(Board board, String target, Map<String, Object> updateMap) {
        Field field = null;
        try {
            field = board.getClass().getDeclaredField(target);
            field.setAccessible(true); // private 필드 접근 허용
            Class<?> fieldType = field.getType();
            if (fieldType == Integer.class) {
                Integer object = (Integer) field.get(board); // 객체의 필드 값 가져오기
                addNeedUpdate(target, object + 1, updateMap);
            } else {
                throw new RuntimeException("integer 타입이 아닙니다");
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void decrease(Board board, String target, Map<String, Object> updateMap) {
        Field field = null;
        try {
            field = board.getClass().getDeclaredField(target);
            field.setAccessible(true); // private 필드 접근 허용
            Class<?> fieldType = field.getType();
            if (fieldType == Integer.class) {
                Integer object = (Integer) field.get(board); // 객체의 필드 값 가져오기
                addNeedUpdate(target, object - 1, updateMap);
            } else {
                throw new RuntimeException("integer 타입이 아닙니다");
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
