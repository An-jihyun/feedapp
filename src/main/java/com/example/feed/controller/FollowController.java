package com.example.feed.controller;


import com.example.feed.dto.common.ApiResponse;
import com.example.feed.dto.follows.FollowingListResponseDto;
import com.example.feed.dto.follows.FollowsRequestDto;
import com.example.feed.dto.follows.FollowsResponseDto;
import com.example.feed.dto.follows.UnfollowUserNameReqeustDto;
import com.example.feed.security.userDetail.CustomUserDetails;
import com.example.feed.service.AuthService;
import com.example.feed.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class FollowController {

    private final AuthService authService;
    private final FollowService followService;


    // 팔로우 생성하기
    @PostMapping("/follows")
    public ResponseEntity<ApiResponse<FollowsResponseDto>> follow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FollowsRequestDto requestDto
            ) {
        Long followerId = userDetails.getUserId();
        FollowsResponseDto followed = followService.follow(followerId, requestDto);

        return new ResponseEntity<>(new ApiResponse<>("팔로우정상완료",followed),HttpStatus.CREATED);
    }

    //팔로잉 조회
    @GetMapping("/{userId}/followings")
    public ResponseEntity<ApiResponse<List<FollowingListResponseDto>>> findFollowsListByUserId(
            @PathVariable Long userId
    ) {
        List<FollowingListResponseDto> followingList = followService.findListByUserId(userId);

        return new  ResponseEntity<>(new ApiResponse<>("팔로잉목록조회성공",followingList),HttpStatus.OK);
    }
    //팔로워 조회
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<List<FollowingListResponseDto>>> findFollowerListByUserId(@PathVariable Long userId) {
        List<FollowingListResponseDto> followersList = followService.findfollowerListByUserId(userId);

        return new ResponseEntity<>(new ApiResponse<>("팔로워목록조회성공",followersList),HttpStatus.OK );
    }
    //팔로우 삭제
    @DeleteMapping("/follows")
    public ResponseEntity<ApiResponse<Void>> deleteFollowers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UnfollowUserNameReqeustDto reqeustDto) {

            followService.deleteFollowers(userDetails.getUserId(),userDetails.getUsername());

            return new  ResponseEntity<>(new ApiResponse<>("언팔로우성공",null),HttpStatus.NO_CONTENT);
    }
}
