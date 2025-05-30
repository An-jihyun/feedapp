package com.example.feed.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndDeletedFalse(Long id);
    Page<Post> findAllByDeletedFalse(Pageable pageable);
    Page<Post> findByCreatedAtBetweenAndDeletedFalse(LocalDateTime periodStart, LocalDateTime periodEnd, Pageable pageable);
}
