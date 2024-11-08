package com.example.demo.dto;

import com.example.demo.entity.Board;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class GetMultipleBoardResponseDto {
    private final List<BoardData> boards;
    private final Meta meta;

    @RequiredArgsConstructor
    @Builder
    @Data
    public static class BoardData {
        private final String id;
        private final String title;
        private final String author;

        private final String generatedAt;
        private final String lastModified;

        private final Integer views;
        private final Integer thumbsUp;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Meta {
        private final int page;
        private final int count;
        private final boolean isLast;
    }

    public static GetMultipleBoardResponseDto fromEntity(Page<Board> boards) {
        List<BoardData> boardData = new ArrayList<>();
        for (var board : boards) {
            boardData.add(BoardData.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .author(board.getAuthor())
                    .generatedAt(board.getGeneratedAt())
                    .lastModified(board.getLastModified())
                    .views(board.getViews())
                    .thumbsUp(board.getThumbsUp())
                    .build());
        }
        return new GetMultipleBoardResponseDto(boardData, new Meta(boards.getNumber() + 1, boardData.size(), !boards.hasNext()));
    }
}
