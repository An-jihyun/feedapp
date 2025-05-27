package com.example.feed.dto.post.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreatePostRequestDto {

    @Column(nullable = false)
    @Size(max = 20, message = "제목은 20자 이내로 작성가능합니다.")
    private String title;

    @Column(nullable = false)
    @Size(min = 5, max = 200, message = "작성글은 5~200자 이내로 작성가능합니다.")
    private String content;

}
