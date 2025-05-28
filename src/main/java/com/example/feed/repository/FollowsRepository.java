package com.example.feed.repository;

import com.example.feed.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowsRepository extends JpaRepository<Follow,Long> {

    List<Follow> findByFollowerId(Long followerId);


}
