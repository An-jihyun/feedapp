package com.example.feed.like.dto;

import com.example.feed.like.LikeTargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeRequestDto {

    @NotNull(message = "대상 타입을 지정해주세요.")
    private LikeTargetType targetType;

    @NotNull(message = "대상 ID를 지정해주세요.")
    private Long targetId;
}
