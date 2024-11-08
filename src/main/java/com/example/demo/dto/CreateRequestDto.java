package com.example.demo.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
@Data
public class CreateRequestDto {
    @NotEmpty
    private final String title;
    @NotEmpty
    private final String author;
    @NotEmpty
    private final String contents;
}
