package com.example.feed.dto.comment;

import com.example.feed.entity.Comment;
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

    private CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userName = comment.getUser().getUserName();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }

    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(comment);
    }
}
