package com.example.demo.repository;

import com.example.demo.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends CrudRepository<Board, String> {
    //조회
    //조회수 up
    //thumb up
    Page<Board> findByAuthor(String author, Pageable pageable);
    Page<Board> findByTitleContainingOrContentsContaining(String title,String contents, Pageable pageable);
    Page<Board> findByTitleContaining(String title, Pageable pageable);
    Page<Board> findByContentsContaining(String contents, Pageable pageable);

    void deleteById(String id);
}
