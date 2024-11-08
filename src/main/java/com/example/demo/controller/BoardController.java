package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService service;

    //타이틀 또는 내용 검색
    //저자검색
    @GetMapping("/search")
    public GetMultipleBoardResponseDto searchByTitleAndContents(
            @RequestParam(defaultValue = "", required = false) String author,
            @RequestParam(defaultValue = "", required = false) String contents,
            @RequestParam(defaultValue = "", value = "title", required = false) String title,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(defaultValue = "1", required = false) Integer page) {
        page--;
        return service.searchByTitleAndContents(author, title, contents, page, size);
    }

    @GetMapping
    public GetBoardResponseDto searchById(@RequestParam String id) {
        return service.searchById(id);
    }

    @DeleteMapping()
    public void deleteById(@RequestParam String id) {
        service.deleteById(id);
    }

    @PostMapping
    public GetBoardResponseDto save(@Validated @RequestBody CreateRequestDto requestDto, BindingResult result) {
        return service.save(requestDto);
    }

    @PutMapping
    public String update(@Validated @RequestBody BoardUpdateRequestDto requestDto, BindingResult result) {
        return service.update(requestDto);
    }
}
