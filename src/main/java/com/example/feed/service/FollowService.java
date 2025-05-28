package com.example.feed.service;


import com.example.feed.dto.follows.FollowsResponseDto;
import com.example.feed.entity.Follow;
import com.example.feed.entity.User;
import com.example.feed.repository.FollowsRepository;
import com.example.feed.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class FollowService {

    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;

    public FollowService(FollowsRepository followsRepository, UserRepository userRepository) {
        this.followsRepository = followsRepository;
        this.userRepository = userRepository;
    }


    public FollowsResponseDto follow(Long followerId, Long followingId) {
        //사용자
        Optional<User> followerUserId = userRepository.findById(followerId);
        //팔로잉 대상자
        Optional<User> followingUserId = userRepository.findById(followingId);

        //예외처리

        User loginUserId = followerUserId.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요."));
        User followedId = followingUserId.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "팔로우 대상이 없습니다."));


        //자신이 자신 팔로우 금지
        if (loginUserId.getId().equals(followedId.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"자신은 팔로우 할 수 없습니다.");
        }
        //follow 객체 생성
        Follow follow = new Follow(loginUserId, followedId);
        followsRepository.save(follow);
        return new FollowsResponseDto(followedId);
    }
}
