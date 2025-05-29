package com.example.feed.dto.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCommentRequestDto {

    @NotNull(message = "댓글 내용을 입력하세요")
    @Size(max = 50, message = "댓글은 50자 이내로 작성가능합니다.")
    private String content;
}
