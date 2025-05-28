package com.example.feed.dto.follows;



import com.example.feed.entity.User;

public class FollowsResponseDto {

    private Long id;
    private String username;

    public FollowsResponseDto(Long id,String username){
        this.id = id;
        this.username = username;
    }

    public FollowsResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUserName();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
