package com.example.demo.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateRequestDto {
    @NotEmpty
    private String id;
    private String title = "";
    private String contents = "";
}
