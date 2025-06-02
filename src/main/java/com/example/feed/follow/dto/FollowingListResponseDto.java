package com.example.feed.follow.dto;

import com.example.feed.user.User;

public class FollowingListResponseDto {

	private final Long userId;
	private final String username;

	private FollowingListResponseDto(Long userId, String username) {
		this.userId = userId;
		this.username = username;
	}

	public static FollowingListResponseDto from(User user) {
		return new FollowingListResponseDto(user.getId(), user.getUserName());
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

}