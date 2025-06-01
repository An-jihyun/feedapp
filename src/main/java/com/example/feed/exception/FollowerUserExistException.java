package com.example.feed.exception;

public class FollowerUserExistException extends RuntimeException{
    public FollowerUserExistException(String message){
        super(message);
    }
}
