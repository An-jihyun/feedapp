package com.example.feed.like.dto;

import com.example.feed.like.LikeTargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponseDto {

    private LikeTargetType targetType;
    private Long targetId;
    private long likeCount;
    private boolean isLiked;

    public static LikeResponseDto of(LikeTargetType targetType, Long targetId, long likeCount, boolean isLiked) {
        return new LikeResponseDto(targetType, targetId, likeCount, isLiked);
    }
}
