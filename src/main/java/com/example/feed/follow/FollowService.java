package com.example.feed.follow;


import com.example.feed.follow.dto.FollowingListResponseDto;
import com.example.feed.follow.dto.FollowsRequestDto;
import com.example.feed.follow.dto.FollowsResponseDto;

import com.example.feed.security.userDetail.CustomUserDetails;
import com.example.feed.user.User;
import com.example.feed.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@Transactional
public class FollowService {

    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;
    private final FollowsDomainUtils followsDomainUtils;

    public FollowService(FollowsRepository followsRepository, UserRepository userRepository, FollowsDomainUtils followsDomainUtils) {
        this.followsRepository = followsRepository;
        this.userRepository = userRepository;
        this.followsDomainUtils = followsDomainUtils;
    }


    //팔로잉 추가
    public FollowsResponseDto follow(CustomUserDetails userDetails, FollowsRequestDto requestDto) {
        //로그인 사용자 Id
        Long followerId = userDetails.getUserId();
        //팔로잉 대상 Id
        Long followingId = requestDto.getUserId();
        //자기 자신 팔로우 금지 도메인 유틸 메서드로 활용
        followsDomainUtils.validateNotSelfFollow(followerId,followingId);
        //팔로우 중복 금지 예외처리
        followsDomainUtils.validateAlreadyFollowing(followerId,followingId);
        //유저 조회 도메인 유틸메서드로 활용
        User follower =  followsDomainUtils.validNotLoginUser(followerId);
        User following = followsDomainUtils.validFollowUserNotFound(followingId);

        //follow 객체 생성
        Follow follow = Follow.of(follower,following);
        followsRepository.save(follow);
        return FollowsResponseDto.from(follow);
    }

    //팔로잉 목록 전체 조회
    @Transactional(readOnly=true)
    public List<FollowingListResponseDto> getFollowings(CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        return followsRepository.findAllByFollowerIdAndDeletedFalse(userId).stream()
                .map(follow -> FollowingListResponseDto.from(follow.getFollowing())).toList();
    }

    //팔로잉 단건 조회
    @Transactional(readOnly=true)
    public FollowsResponseDto getFollowing(CustomUserDetails userDetails,Long followingId) {
        Long userId = userDetails.getUserId();
        //팔로잉 여부 유틸 메서드로 활용
        Follow follow = followsDomainUtils.validNotFollowingUser(userId,followingId);
        return FollowsResponseDto.from(follow);

    }

    //팔로워 목록 전체 조회
    @Transactional(readOnly=true)
    public List<FollowingListResponseDto> getFollowers(CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return followsRepository.findByFollowingIdAndDeletedFalse(userId).stream()
                .map(follow -> FollowingListResponseDto.from(follow.getFollower())).toList();
    }

    //팔로워 단건 조회
    @Transactional(readOnly=true)
    public FollowsResponseDto getFollower(CustomUserDetails userDetails, Long followerId) {
        Long userId = userDetails.getUserId();
        //팔로워 여부 유틸 메서드로 활용
        Follow follow =followsDomainUtils.validNotFollowerUser(userId,followerId);
        return FollowsResponseDto.fromFollower(follow);
    }

    //팔로우 삭제
    @Transactional
    public void softDeleteUnFollow(CustomUserDetails userDetails,String username) {
        Long userId = userDetails.getUserId();
        //팔로우 관계 여부 유틸 메서드로 활용
        Follow follow = followsDomainUtils.validFollowerExistUserAndNotFollowerUser(userId, username);
        follow.softDelete();
    }

    //jh-user삭제시 연관 팔로우 삭제
    @Transactional
    public void softDeleteFollowsByUserId(Long userId) {
        List<Follow> follows = followsRepository.findAllByUserId(userId); // 아래 설명
        for (Follow follow : follows) {
            follow.softDelete();
        }
    }

}
