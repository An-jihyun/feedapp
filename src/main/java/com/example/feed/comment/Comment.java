package com.example.feed.comment;

import com.example.feed.common.BaseEntity;
import com.example.feed.post.Post;
import com.example.feed.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA가 내부적으로 엔티티 객체를 생성할 때 기본 생성자가 필요합니다.
 * 직접 new로 생성하는 것을 막기 위해 protected로 설정했습니다.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private Comment(String content, User user, Post post) {
        this.content = content;
        this.user = user;
        this.post = post;
    }

    public static Comment create(String content, User user, Post post) {

        return new Comment(content, user, post);
    }

    public void updateContent(String content) {

        this.content = content;
    }
}
