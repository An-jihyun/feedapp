package com.example.feed.entity;

import com.example.feed.dto.post.request.UpdatePostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor
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
    */
    public void patchCheck(UpdatePostRequestDto uDto) {

        if(uDto.getTitle() != null) {
            this.title = uDto.getTitle();
        }

        if(uDto.getContent() != null) {
            this.content = uDto.getContent();
        }

    }

}
