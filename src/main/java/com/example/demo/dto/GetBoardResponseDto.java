package com.example.demo.dto;

import com.example.demo.entity.Board;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetBoardResponseDto {
    private final String id;
    private final String title;
    private final String author;
    private final String contents;

    private final String generatedAt;
    private final String lastModified;

    private final Integer views;
    private final Integer thumbsUp;

    public static GetBoardResponseDto fromEntity(Board board) {
        return GetBoardResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .author(board.getAuthor())
                .contents(board.getContents())
                .generatedAt(board.getGeneratedAt().toString())
                .lastModified(board.getLastModified())
                .views(board.getViews())
                .thumbsUp(board.getThumbsUp())
                .build();
    }
}
