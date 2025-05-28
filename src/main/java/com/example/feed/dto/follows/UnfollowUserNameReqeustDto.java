package com.example.feed.dto.follows;

public class UnfollowUserNameReqeustDto {

    private String username;

    public UnfollowUserNameReqeustDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
