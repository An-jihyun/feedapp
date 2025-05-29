package com.example.feed.service;


import com.example.feed.dto.follows.FollowingListResponseDto;
import com.example.feed.dto.follows.FollowsRequestDto;
import com.example.feed.dto.follows.FollowsResponseDto;
import com.example.feed.entity.Follow;
import com.example.feed.entity.User;
import com.example.feed.repository.FollowsRepository;
import com.example.feed.repository.UserRepository;
import com.example.feed.security.userDetail.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class FollowService {

    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;

    public FollowService(FollowsRepository followsRepository, UserRepository userRepository) {
        this.followsRepository = followsRepository;
        this.userRepository = userRepository;
    }


    //팔로잉 추가
    public FollowsResponseDto follow(CustomUserDetails userDetails, FollowsRequestDto requestDto) {
        //로그인 사용자 Id
        Long followerId = userDetails.getUserId();
        //팔로잉 대상 Id
        Long followingId = requestDto.getuserId();
        //자기 자신 팔로우 금지
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"자신은 팔로우 할 수 없습니다.");
        }
        //사용자조회
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "로그인 후 이용해주세요"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팔로우 대상이 없습니다."));

        //follow 객체 생성
        Follow follow = new Follow(follower, following);
        followsRepository.save(follow);
        return new FollowsResponseDto(following);
    }

    //팔로잉 목록 전체 조회
    public List<FollowingListResponseDto> getFollowings(CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return followsRepository.findAllByFollowerId(userId).stream()
                .map(follow -> FollowingListResponseDto.from(follow.getFollowing())).toList();
    }

    //팔로잉 단건 조회
    public FollowsResponseDto getFollowing(CustomUserDetails userDetails,Long followingId) {
        Long userId = userDetails.getUserId();
        Follow follow = followsRepository.findByFollowerIdAndFollowingId(userId, followingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팔로우중이아닙니다."));
        return new FollowsResponseDto(follow.getFollowing());

    }

    //팔로워 목록 전체 조회
    public List<FollowingListResponseDto> getFollowers(CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return followsRepository.findByFollowingId(userId).stream()
                .map(follow -> FollowingListResponseDto.from(follow.getFollower())).toList();
    }

    //팔로워 단건 조회
    public FollowsResponseDto getFollower(CustomUserDetails userDetails, Long followerId) {
        Long userId = userDetails.getUserId();

        Follow follow = followsRepository.findByFollowerIdAndFollowingId(userId, followerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팔로워가 아닙니다."));
        return new FollowsResponseDto(follow.getFollower());
    }

    //팔로우 삭제
    public void unFollow(CustomUserDetails userDetails,String username) {
        Long userId = userDetails.getUserId();
        //unfollow 유저 찾기
        User followingUser = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        //follow 관계 찾기
        Follow follow = followsRepository.findByFollowerIdAndFollowingId(userId, followingUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팔로우 중이 아닙니다. "));
        //삭제
        followsRepository.delete(follow);

    }


}
