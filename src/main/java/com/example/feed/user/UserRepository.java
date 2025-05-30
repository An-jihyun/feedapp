package com.example.feed.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 기존: 모든 사용자 조회
    Optional<User> findByEmail(String email);

    // 추가: 탈퇴하지 않은 사용자만 조회
    Optional<User> findByEmailAndIsDeletedFalse(String email);
}
