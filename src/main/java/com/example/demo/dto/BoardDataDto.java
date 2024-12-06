package com.example.demo.dto;

import com.example.demo.entity.Board;
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

    public static BoardDataDto fromEntity(Board board) {
        return new BoardDataDto(
                board.getId(),
                board.getTitle(),
                board.getAuthor(),
                board.getGeneratedAt(),
                board.getLastModified(),
                board.getViews(),
                board.getThumbsUp());
    }

    public static BoardDataDto empty() {
        return new BoardDataDto("", "", "", "", "", 0, 0);
    }
}