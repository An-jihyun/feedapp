package com.example.feed.repository;

import com.example.feed.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
