package com.example.feed.exception.follows;

public class FollowNotFollowerException extends RuntimeException{
    public FollowNotFollowerException(String message){
        super(message);
    }
}
