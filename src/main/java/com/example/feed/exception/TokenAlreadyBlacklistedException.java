package com.example.feed.exception;

public class TokenAlreadyBlacklistedException extends RuntimeException {

    public TokenAlreadyBlacklistedException(String message) {
        super(message);
    }

}
