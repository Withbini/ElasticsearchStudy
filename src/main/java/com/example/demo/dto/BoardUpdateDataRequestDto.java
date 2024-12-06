package com.example.demo.dto;

import com.example.demo.entity.ActionType;
import lombok.Data;

@Data
public class BoardUpdateDataRequestDto {
    private String id;
    private ActionType action;
    private String target;
    private String value;
}
