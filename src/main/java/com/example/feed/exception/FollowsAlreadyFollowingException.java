package com.example.feed.exception;

public class FollowsAlreadyFollowingException extends RuntimeException {

	public FollowsAlreadyFollowingException(String message) {
		super(message);
	}

}