package com.example.feed.follow;




import com.example.feed.common.BaseEntity;
import com.example.feed.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "follows") //게터는 필요시 따로 생성 예정 더 확실한 캡슐화를 위해
public class Follow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "follower_id",nullable = false)
    @NotNull
    //팔로우를 건 사람(로그인한 사용자)
    private User follower;


    @ManyToOne
    @JoinColumn(name = "following_id",nullable = false)
    @NotNull
    //팔로우를 당한 사람(상대방,팔로우 대상)
    private User following;

    @Column(nullable = false)
    private boolean deleted = false;

    private Follow(User follower, User following){
        this.follower = follower;
        this.following = following;
    }



    public static Follow of(User follower, User following) {
        return new Follow(follower,following);
    }
    public void softDelete(){
        this.deleted =true;
    }

    //Jpa 가 사용 예정
    public Follow() {
    }

    //Getter추가

    public User getFollowing() {
        return following;
    }

    public User getFollower() {
        return follower;
    }
}
