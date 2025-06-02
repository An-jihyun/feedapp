package com.example.feed.post;

import com.example.feed.common.BaseEntity;
import com.example.feed.post.dto.request.UpdatePostRequestDto;
import com.example.feed.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    //private 접근제어자 부여
    private Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    //메서드로만 생성
    public static Post create(String title, String content, User user) {
        return new Post(title, content, user);
    }

    /*
    업데이트 요청객체를 받아 null 값이 아닌 필드를 변경해주는 메서드
    -> 계층 간 분리 타계층의 의존존관계를 줄이자 (UpdatePostRequestDto uDto) -> (String title, String content)
    -> 변수명도 변경 patchCheck 의미 모호 -> patchIfNotNull
    */
    public void patchIfNotNull(String title, String content) {

        if(title != null) {
            this.title = title;
        }

        if(content != null) {
            this.content = content;
        }

    }

}
