package com.example.feed.security;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table(name = "token_blacklist")
@Entity
public class TokenBlacklist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String token;

	private LocalDateTime expiration;

	protected TokenBlacklist() {
	}

	public TokenBlacklist(String token, LocalDateTime expiration) {
		this.token = token;
		this.expiration = expiration;
	}

}