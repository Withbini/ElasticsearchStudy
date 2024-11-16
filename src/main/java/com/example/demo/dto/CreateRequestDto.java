package com.example.demo.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
@Data
public class CreateRequestDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String author;
    @NotEmpty
    private String contents;
}
