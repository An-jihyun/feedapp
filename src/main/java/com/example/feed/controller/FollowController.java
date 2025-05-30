package com.example.feed.controller;


import com.example.feed.dto.common.ApiResponse;
import com.example.feed.dto.follows.FollowingListResponseDto;
import com.example.feed.dto.follows.FollowsRequestDto;
import com.example.feed.dto.follows.FollowsResponseDto;
import com.example.feed.dto.follows.UnfollowUserNameRequestDto;
import com.example.feed.security.userDetail.CustomUserDetails;
import com.example.feed.service.AuthService;
import com.example.feed.service.FollowService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<FollowsResponseDto>> followCreateAPI(
           @AuthenticationPrincipal CustomUserDetails userDetails,
           @Valid @RequestBody FollowsRequestDto requestDto
            ) {
        FollowsResponseDto followed = followService.follow(userDetails, requestDto);
        return new ResponseEntity<>(new ApiResponse<>("팔로우정상완료",followed),HttpStatus.CREATED);
    }

    //로그인한 유저의 팔로잉 전체조회

    @GetMapping("/me/followings")

    public ResponseEntity<ApiResponse<List<FollowingListResponseDto>>> createFollowAPI(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<FollowingListResponseDto> followingList = followService.getFollowings(userDetails);

        return new  ResponseEntity<>(new ApiResponse<>("팔로잉 목록 조회 성공",followingList),HttpStatus.OK);
    }

    //팔로잉 단건 조회
    @GetMapping("/me/following/{followingId}")
    public ResponseEntity<ApiResponse<FollowsResponseDto>> followingByIdAPI(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long followingId) {
        FollowsResponseDto userIdAndFollowingUserId = followService.getFollowing(userDetails, followingId);
        return new ResponseEntity<> (new ApiResponse<>("팔로잉 단건 조회 성공",userIdAndFollowingUserId),HttpStatus.OK);

    }
    //팔로워 전체 조회
    @GetMapping("/me/followers")
    public ResponseEntity<ApiResponse<List<FollowingListResponseDto>>> followerListAPI(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FollowingListResponseDto> followersList = followService.getFollowers(userDetails);
        return new ResponseEntity<>(new ApiResponse<>("팔로워 목록 조회 성공",followersList),HttpStatus.OK );
    }

    //팔로워 단건 조회
    @GetMapping("/me/followers/{followerId}")
    public ResponseEntity<ApiResponse<FollowsResponseDto>> followerByIdAPI(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long followerId
    ) {
        FollowsResponseDto followerUser = followService.getFollower(userDetails, followerId);
        return new ResponseEntity<>(new ApiResponse<>("팔로워 단건 조회 성공",followerUser),HttpStatus.OK);
    }

    //팔로우 삭제
    @DeleteMapping("/follows")
    public ResponseEntity<ApiResponse<Void>> followDeleteAPI(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UnfollowUserNameRequestDto reqeustDto) {

            followService.softDeleteUnFollow(userDetails,reqeustDto.getUsername());

            return new  ResponseEntity<>(new ApiResponse<>("언팔로우 성공",null),HttpStatus.OK);
    }
}