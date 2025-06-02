package com.example.feed.security.jwt;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.feed.security.TokenBlacklistRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlacklistCleanupScheduler {

	private final TokenBlacklistRepository blacklistRepository;

	@Scheduled(fixedRate = 3600000) // 1시간마다
	public void cleanExpiredTokens() {
		LocalDateTime now = LocalDateTime.now();

		// DB 에서 직접 만료된 토큰들을 삭제
		int deletedCount = blacklistRepository.deleteByExpirationBefore(now);

		if (deletedCount > 0) {
			log.info("만료된 토큰 {} 개 정리 완료", deletedCount);
		}
	}

}