package com.example.feed.security.jwt;

import com.example.feed.entity.TokenBlacklist;
import com.example.feed.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BlacklistCleanupScheduler {

    private final TokenBlacklistRepository blacklistRepository;

    @Scheduled(fixedRate = 3600000) // 1시간마다
    public void cleanExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<TokenBlacklist> expired = blacklistRepository.findAll()
                .stream()
                .filter(t -> t.getExpiration().isBefore(now))
                .toList();

        blacklistRepository.deleteAll(expired);
    }
}