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



    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(comment.getId(),
                comment.getContent(),
                comment.getUser().getUserName(),
                comment.getCreatedAt(),
                comment.getModifiedAt());
    }

}
