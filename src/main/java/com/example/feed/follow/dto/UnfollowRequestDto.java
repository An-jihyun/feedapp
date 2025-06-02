package com.example.feed.follow.dto;

public class UnfollowRequestDto {

	private String username;

	public UnfollowRequestDto(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}