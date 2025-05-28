package com.example.feed.controller;


import com.example.feed.dto.follows.FollowsRequestDto;
import com.example.feed.dto.follows.FollowsResponseDto;
import com.example.feed.security.CustomUserDetails;
import com.example.feed.service.AuthService;
import com.example.feed.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class FollowController {

    private final AuthService authService;
    private final FollowService followService;


    // 팔로우 생성하기
    @PostMapping("/follows")
    public ResponseEntity<FollowsResponseDto> follow(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FollowsRequestDto requestDto
            ) {
        //사용자 id
//        Long followerId = userDetails.getUserId();
        Long followerId = 1L;
        //타 사용자를 팔로잉할 id
        Long followingId = requestDto.getuserId();

        FollowsResponseDto followed = followService.follow(followerId, followingId);

        return new ResponseEntity<>(followed,HttpStatus.CREATED);
    }
}
