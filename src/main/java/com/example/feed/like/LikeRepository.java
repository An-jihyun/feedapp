package com.example.feed.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 사용자가 특정 대상에 좋아요를 눌렀는지 확인
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, LikeTargetType targetType, Long targetId);

    // 특정 사용자의 특정 대상 좋아요 조회
    Optional<Like> findByUserIdAndTargetTypeAndTargetId(Long userId, LikeTargetType targetType, Long targetId);

    // 특정 대상의 전체 좋아요 수 조회
    long countByTargetTypeAndTargetId(LikeTargetType targetType, Long targetId);

    // 특정 대상의 모든 좋아요 삭제 (게시물/댓글 삭제 시 사용)
    void deleteAllByTargetTypeAndTargetId(LikeTargetType targetType, Long targetId);

    // 특정 사용자의 모든 좋아요 삭제 (사용자 삭제 시 사용)
    void deleteAllByUserId(Long userId);
}
