package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class SearchRequestDto {
    //Im not sure this is right way not to designate final keyword
    private String title;
    private String contents;
    private String author;
    private Integer page = 0;
    private Integer size = 10;
}
