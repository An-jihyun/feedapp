package com.example.feed.repository;

import com.example.feed.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowsRepository extends JpaRepository<Follow,Long> {

    //팔로잉 목록 조회
    List<Follow> findByFollowerId(Long followerId);

    //팔로워 목록 조회
    List<Follow> findByFollowingId(Long followingId);

    Optional<Follow> findByUserName(String username);

    Follow findByUnFollows(Optional<Follow> follower, Optional<Follow> unfollowUserName);
}
