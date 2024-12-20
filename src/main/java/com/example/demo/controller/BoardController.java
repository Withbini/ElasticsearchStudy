package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.SearchType;
import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
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
            @RequestParam(defaultValue = "0", required = false) Integer page) {
        return service.searchByTitleAndContents(author, title, contents, page, size);
    }

    @GetMapping("/search2")
    public SearchResult<BoardDataDto> search(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "TITLE_AND_CONTENT") SearchType searchType,
            @RequestParam(required = false, defaultValue = "generatedAt") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.search(keyword, searchType, sortField, sortOrder, pageable);
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

    @PutMapping("/data")
    public BoardDataDto updateData(@Validated @RequestBody BoardUpdateDataRequestDto requestDto, BindingResult result) {
        var data = service.updateData(requestDto);
        log.info("JBJB data :{}", data);
        return data;
    }

    @GetMapping("/aggregate")
    public Long getSummary(@RequestParam String param) {
        return service.getTotalSummary(param);
    }
}
