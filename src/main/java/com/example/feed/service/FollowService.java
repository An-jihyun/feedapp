package com.example.feed.service;


import com.example.feed.dto.follows.FollowingListResponseDto;
import com.example.feed.dto.follows.FollowsRequestDto;
import com.example.feed.dto.follows.FollowsResponseDto;
import com.example.feed.entity.Follow;
import com.example.feed.entity.User;
import com.example.feed.repository.FollowsRepository;
import com.example.feed.repository.UserRepository;
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
    public FollowsResponseDto follow(Long followerId, FollowsRequestDto requestDto) {
        Long followingId = requestDto.getuserId();
        //사용자
        Optional<User> followerUser = userRepository.findById(followerId);
        //팔로잉 대상자
        Optional<User> followingUser = userRepository.findById(followingId);

        //예외처리
        User loginUserId = followerUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요."));
        User followedId = followingUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "팔로우 대상이 없습니다."));

        //자기 자신 팔로우 금지
        if (loginUserId.getId().equals(followedId.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"자신은 팔로우 할 수 없습니다.");
        }
        //follow 객체 생성
        Follow follow = new Follow(loginUserId, followedId);
        followsRepository.save(follow);
        return new FollowsResponseDto(followedId);
    }

    //팔로잉 목록 전체 조회
    public List<FollowingListResponseDto> findListByUserId(Long userId) {

        return followsRepository.findByFollowerId(userId).stream()
                .map(follow -> FollowingListResponseDto.from(follow.getFollowing())).toList();
    }

    //팔로잉 단건 조회
    public FollowsResponseDto findByfollowingUser(Long userId,Long followingId) {
        Optional<Follow> optFollowingUserId = followsRepository.findById(userId);
        //예외처리
        if (optFollowingUserId.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Follow follow = followsRepository.findByFollowerIdAndFollowingId(userId, followingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팔로우중이아닙니다."));

        return new FollowsResponseDto(follow.getFollowing());

    }

    //팔로워 목록 조회
    public List<FollowingListResponseDto> findfollowerListByUserId(Long userId) {
        return followsRepository.findByFollowingId(userId).stream()
                .map(follow -> FollowingListResponseDto.from(follow.getFollower())).toList();
    }

    //팔로워 단건 조회

    public FollowsResponseDto findByFollowerUser(Long userId, Long followerId) {
        Optional<Follow> optFollowerUserId = followsRepository.findById(userId);
        //예외처리
        if (optFollowerUserId.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Follow follow = followsRepository.findByFollowerIdAndFollowingId(userId, followerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팔로우중이아닙니다."));

        return new FollowsResponseDto(follow.getFollowing());
    }

    //팔로우 삭제
    public void deleteFollowers(Long followerId,String username) {
        //following 유저 찾기
        User followingUser = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        //follow 관계 찾기
        Follow follow = followsRepository.findByFollowerIdAndFollowingId(followerId, followingUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "팔로우 중이 아닙니다. "));
        //삭제
        followsRepository.delete(follow);

    }


}
