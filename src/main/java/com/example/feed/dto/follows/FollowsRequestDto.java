package com.example.feed.dto.follows;



public class FollowsRequestDto {

    //팔로우할 대상의 user_id
    private Long userId;

    public FollowsRequestDto(Long userId) {
        this.userId = userId;
    }

    public Long getuserId() {
        return userId;
    }
}
