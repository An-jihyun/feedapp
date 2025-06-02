package com.example.feed.security;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

	boolean existsByToken(String token);

	@Modifying
	@Query("DELETE FROM TokenBlacklist t WHERE t.expiration < ?1")
	int deleteByExpirationBefore(LocalDateTime expiration);

}