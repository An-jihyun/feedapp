package com.example.feed.common;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ErrorResponse {

	private String message;
	private LocalDateTime timestamp;
	private String path;

	public ErrorResponse(String message, String path) {
		this.message = message;
		this.path = path;
		this.timestamp = LocalDateTime.now();
	}

}