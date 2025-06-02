package com.example.feed.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Jackson 역직렬화를 위한 기본 생성자입니다.
 * 일반 코드에서 직접 new 로 생성하지 않도록 protected 로 제한합니다.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CreateCommentRequestDto {

	@NotNull(message = "댓글 내용을 입력하세요")
	@Size(max = 50, message = "댓글은 50자 이내로 작성가능합니다.")
	private String content;

}