package com.example.feed.user;

import com.example.feed.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndDeletedFalse(Long id);
}
