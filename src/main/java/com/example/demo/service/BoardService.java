package com.example.demo.service;

import com.example.demo.dto.CreateRequestDto;
import com.example.demo.dto.GetBoardResponseDto;
import com.example.demo.dto.GetMultipleBoardResponseDto;
import com.example.demo.dto.BoardUpdateRequestDto;
import com.example.demo.entity.Board;
import com.example.demo.repository.BoardRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.document.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final ElasticsearchOperations operations;

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
}
