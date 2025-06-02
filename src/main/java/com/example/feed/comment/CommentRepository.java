package com.example.feed.comment;

import com.example.feed.post.Post;
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

    //Post 도메인에서 사용할 메서드 deleted = false 인 코멘트를 리스트에 담아 반환
    List<Comment> findAllByDeletedFalse();
}