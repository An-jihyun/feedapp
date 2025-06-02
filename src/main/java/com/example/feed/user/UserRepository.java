package com.example.feed.user;

import com.example.feed.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 기존: 모든 사용자 조회
    Optional<User> findByEmail(String email);
    // 탈퇴하지 않은 사용자만 조회 (이메일 기준)
    Optional<User> findByEmailAndDeletedFalse(String email);
   // 탈퇴하지 않은 사용자만 조회 (ID 기준)
    Optional<User> findByIdAndDeletedFalse(Long id);
    //언팔로우(username 기준)
    Optional<User> findByUserNameAndDeletedFalse(String userName);
}
