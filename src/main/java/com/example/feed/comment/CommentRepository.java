package com.example.feed.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    //페이징
    Page<Comment> findByPostIdAndDeletedFalse(Long postId, Pageable pageable);
    Page<Comment> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    //soft-delete용
    List<Comment> findByPostIdAndDeletedFalse(Long postId);
    List<Comment> findByUserIdAndDeletedFalse(Long userId);

    //단건 조회
    Optional<Comment> findByIdAndDeletedFalse(Long id);
}