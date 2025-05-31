package com.example.feed.exception.follows;

public class FollowNotLoginException extends RuntimeException{
    public FollowNotLoginException(String message) {
        super(message);
    }
}
