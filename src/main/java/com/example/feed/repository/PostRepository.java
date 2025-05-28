package com.example.feed.repository;

import com.example.feed.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtAfter1, Pageable pageable);
}
