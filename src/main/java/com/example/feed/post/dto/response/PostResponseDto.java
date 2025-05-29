package com.example.feed.post.dto.response;

import com.example.feed.comment.dto.CommentSimpleResponseDto;
import com.example.feed.post.Post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


/*
정적팩토리 메서드 from() 메서드로만 생성: private 접근제어
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;

    //User 테이블의 개별식별자
    private Long userID;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentSimpleResponseDto> comments;

    //entity -> dto
    public static PostResponseDto from(Post post, List<CommentSimpleResponseDto> comments) {
        return new PostResponseDto(post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getId(),
                post.getCreatedAt(),
                post.getModifiedAt(),
                comments);
    }


    // 댓글 없이 사용하는 곳 호환용 overload
    public static PostResponseDto from(Post post) {
        return from(post, null);
    }
}
