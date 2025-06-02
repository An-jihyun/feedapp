package com.example.feed.follow.dto;

import com.example.feed.follow.Follow;
import com.example.feed.user.User;

public class FollowsResponseDto {

	private Long id;
	private String username;

	private FollowsResponseDto(Long id, String username) {
		this.id = id;
		this.username = username;
	}

	public static FollowsResponseDto from(User user) {
		return new FollowsResponseDto(user.getId(), user.getUserName());
	}

	//사용자가 사용 follow 객체로부터 생성
	public static FollowsResponseDto from(Follow follow) {
		return from(follow.getFollowing());
	}

	//팔로워 조회 시 사용
	public static FollowsResponseDto fromFollower(Follow follow) {
		return from((follow.getFollower()));
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

}