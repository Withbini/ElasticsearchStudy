package com.example.demo.repository;

import com.example.demo.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends CrudRepository<Board, String> {
    //조회
    //조회수 up
    //thumb up
    Page<Board> findByAuthorOrderByGeneratedAtDesc(String author, Pageable pageable);
    Page<Board> findByTitleContainingOrContentsContainingOrderByGeneratedAtDesc(String title, String contents, Pageable pageable);
    Page<Board> findByTitleContainingOrderByGeneratedAtDesc(String title, Pageable pageable);
    Page<Board> findByContentsContainingOrderByGeneratedAtDesc(String contents, Pageable pageable);

    void deleteById(String id);
}
