package com.example.feed.exception;

public class FollowNotFollowerException extends RuntimeException{
    public FollowNotFollowerException(String message){
        super(message);
    }
}
