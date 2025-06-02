package com.example.feed.exception;

public class SelfLikeNotAllowedException extends RuntimeException {

	public SelfLikeNotAllowedException(String message) {
		super(message);
	}

}