package com.example.feed.comment.dto;

import com.example.feed.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentSimpleResponseDto {
    private String userName;
    private String content;
    private long likeCount;

    public static CommentSimpleResponseDto from(Comment c) {
        return from(c, 0L);
    }

    public static CommentSimpleResponseDto from(Comment c, long likeCount) {
        return new CommentSimpleResponseDto(
                c.getUser().getUserName(),
                c.getContent(),
                likeCount
        );
    }
}