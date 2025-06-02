package com.example.feed.exception;

public class CommentNotFoundException extends RuntimeException {

	public CommentNotFoundException(String message) {
		super(message);
	}

}