package com.example.feed.exception.follows;

public class FollowerUserExistException extends RuntimeException{
    public FollowerUserExistException(String message){
        super(message);
    }
}
