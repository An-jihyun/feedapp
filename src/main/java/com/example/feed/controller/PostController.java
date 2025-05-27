package com.example.feed.controller;

import com.example.feed.dto.common.ApiResponse;
import com.example.feed.dto.post.request.CreatePostRequestDto;
import com.example.feed.dto.post.response.PostResponseDto;
import com.example.feed.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> save(
            @RequestBody CreatePostRequestDto cDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 저장되었습니다.", postService.save(email, cDto)), HttpStatus.CREATED);
    }

}
