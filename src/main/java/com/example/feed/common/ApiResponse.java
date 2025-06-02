package com.example.feed.common;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private String message;
	private T responseDto;
	private LocalDateTime timestamp;

	public ApiResponse(String message, T responseDto) {
		this.message = message;
		this.responseDto = responseDto;
		this.timestamp = LocalDateTime.now();
	}

}