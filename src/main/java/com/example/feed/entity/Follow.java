package com.example.feed.entity;



import jakarta.persistence.*;

@Entity

//게터는 필요시 따로 생성 예정 더 확실한 캡슐화를 위해
@Table(name = "follows")
public class Follow { //extends BaseEntity 예정

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "follower_id",nullable = false)
    private User follower;


    @ManyToOne
    @JoinColumn(name = "following_id",nullable = false)
    private User following;

    public Follow(User follower, User following){
        this.follower = follower;
        this.following = following;
    }

    //Jpa 가 사용 예정
    public Follow() {

    }
}
