package com.example.feed.exception.follows;

public class FollowNotMySelfException extends RuntimeException {
    public FollowNotMySelfException(String message) {
        super(message);
    }
}
