package com.example.feed.security;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Table(name = "token_blacklist")
@Entity
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiration;

    protected TokenBlacklist() {}

    public TokenBlacklist(String token, LocalDateTime expiration) {
        this.token = token;
        this.expiration = expiration;
    }
}