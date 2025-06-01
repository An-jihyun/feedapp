package com.example.feed.comment.dto;

import com.example.feed.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private long likeCount;


    public static CommentResponseDto from(Comment c) {
        return from(c, 0L);
    }

    public static CommentResponseDto from(Comment c, long likeCount) {
        return new CommentResponseDto(
                c.getId(),
                c.getContent(),
                c.getUser().getUserName(),
                c.getCreatedAt(),
                c.getModifiedAt(),
                likeCount
        );
    }

}
