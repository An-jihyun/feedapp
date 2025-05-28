package com.example.feed.repository;

import com.example.feed.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowsRepository extends JpaRepository<Follow,Long> {


}
