package com.example.feed.follow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowsRepository extends JpaRepository<Follow,Long> {

    //팔로잉 목록 전체 조회
    List<Follow> findAllByFollowerIdAndDeletedFalse(Long followerId);

    //팔로워 목록 조회
    List<Follow> findByFollowingIdAndDeletedFalse(Long followingId);

    //단건 조회 및 예외처리
    Optional<Follow> findByFollowerIdAndFollowingIdAndDeletedFalse(Long followerId, Long followingId);

}
