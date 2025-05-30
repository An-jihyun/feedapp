package com.example.feed.follow;


import com.example.feed.entity.User;
import com.example.feed.exception.follows.*;
import com.example.feed.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class FollowsDomainUtils {

    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;

    public FollowsDomainUtils(FollowsRepository followsRepository, UserRepository userRepository) {
        this.followsRepository = followsRepository;
        this.userRepository = userRepository;
    }

    //팔로우 생성 ->자기 자신 팔로우 금지예외처리
    public void validateNotSelfFollow(Long followerId, Long followingId){

        if (followerId.equals(followingId)) {
            throw new FollowNotMySelfException("자신은 팔로우 할 수 없습니다.");
        }
    }
    //팔로우 생성 -> 로그인 유저 확인
    public User validNotLoginUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new FollowNotLoginException("로그인 후 이용해 주세요" ));
    }

    //팔로우 생성 -> 팔로우 할 대상의 유저 존재여부 예외처리
    public User validFollowUserNotFound(Long followingId) {

        return userRepository.findById(followingId)
                .orElseThrow(() -> new FollowerUserExistException("팔로우 대상이 없습니다."));
    }
    //팔로우 생성 -> 팔로우한 유저 중복 금지 예외처리

    public void validateAlreadyFollowing(Long followerId,Long followingId){
        Optional<Follow> alreadyFollower = followsRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(followerId, followingId);

        if (alreadyFollower.isPresent()){
            throw new FollowsAlreadyFollowingException("이미 팔로우중인 사용자입니다.");
        }
    }
    //팔로잉 단건 조회 -> 사용자의 팔로잉 유저가 아닌 유저 여부 예외처리
    public Follow validNotFollowingUser(Long followerId, Long followingI){
        return followsRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(followerId,followingI)
            .orElseThrow(() -> new FollowNotFollowerException("팔로우중이아닙니다."));

    }
    //팔로워 단건 조회 -> 팔로워 대상 여부 예외처리
    public Follow validNotFollowerUser(Long userId,Long followerId){
        return  followsRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(followerId, userId)
                .orElseThrow(() -> new FollowNotFollowerException( "팔로워가 아닙니다."));
    }

    //팔로우 삭제 -> 로그인 유저 팔로잉 삭제 예외처리
    public Follow validFollowerExistUserAndNotFollowerUser(Long followerId,String username){
        //unfollow 유저 찾기
        User followingUser = userRepository.findByUserName(username)
                .orElseThrow(() -> new FollowerUserExistException("유저를 찾을 수 없습니다."));
        //follow 관계 찾기
        Follow follow = followsRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(followerId, followingUser.getId())
                .orElseThrow(() -> new FollowNotFollowerException("팔로우 중이 아닙니다. "));
        return follow;
    }
}
