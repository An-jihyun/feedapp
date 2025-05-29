package com.example.feed.repository;

import com.example.feed.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostId(Long postId, Pageable pageable);

    Page<Comment> findByUserId(Long userId, Pageable pageable);

    void deleteAllByPostId(Long postId);       // 게시글 기준 전체 댓글 삭제
    void deleteAllByUserId(Long userId);       // 유저 기준 전체 댓글 삭제
}
