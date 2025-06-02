package com.example.feed.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Jackson 바인딩을 위한 기본 생성자입니다.
 * 외부 생성 방지를 위해 protected로 제한합니다.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CreateCommentRequestDto {

    @NotNull(message = "댓글 내용을 입력하세요")
    @Size(max = 50, message = "댓글은 50자 이내로 작성가능합니다.")
    private String content;
}
