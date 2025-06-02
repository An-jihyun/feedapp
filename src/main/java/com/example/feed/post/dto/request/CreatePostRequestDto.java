package com.example.feed.post.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreatePostRequestDto {

	@NotNull(message = "제목을 입력하세요")
	@Size(max = 20, message = "제목은 20자 이내로 작성가능합니다.")
	private String title;

	@NotNull(message = "내용을 입력하세요")
	@Size(min = 5, max = 200, message = "작성글은 5~200자 이내로 작성가능합니다.")
	private String content;

}