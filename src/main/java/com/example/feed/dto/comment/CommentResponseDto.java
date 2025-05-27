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
    private boolean edited;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userName = comment.getUser().getUserName();
        this.createdAt = comment.getCreatedAt();
        this.edited = !comment.getCreatedAt().equals(comment.getModifiedAt());
    }

    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(comment);
    }
}
