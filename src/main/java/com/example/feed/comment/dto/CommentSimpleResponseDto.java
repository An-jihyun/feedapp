package com.example.feed.comment.dto;

import com.example.feed.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentSimpleResponseDto {
    private String userName;
    private String content;

    public static CommentSimpleResponseDto from(Comment comment) {
        return new CommentSimpleResponseDto(
                comment.getUser().getUserName(),
                comment.getContent()
        );
    }
}