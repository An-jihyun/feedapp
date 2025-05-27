package com.example.feed.dto.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse<T> {

    private String message;
    private T responseDto;

}
