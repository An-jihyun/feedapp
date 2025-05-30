package com.example.feed.user.dto;

import lombok.Getter;

@Getter
public class DeleteUserRequestDto {
    private String email;
    private String password;
}