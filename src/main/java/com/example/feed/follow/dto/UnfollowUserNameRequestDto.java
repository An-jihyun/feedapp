package com.example.feed.follow.dto;

public class UnfollowUserNameRequestDto {

    private String username;

    public UnfollowUserNameRequestDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
