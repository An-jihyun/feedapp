package com.example.feed.exception.follows;

public class FollowsAlreadyFollowingException extends RuntimeException{
    public FollowsAlreadyFollowingException(String message){
        super(message);
    }
}
