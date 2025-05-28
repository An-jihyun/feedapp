package com.example.feed.controller;

import com.example.feed.dto.common.ApiResponse;
import com.example.feed.dto.post.request.CreatePostRequestDto;
import com.example.feed.dto.post.request.UpdatePostRequestDto;
import com.example.feed.dto.post.response.PostResponseDto;
import com.example.feed.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> save(
            @Valid @RequestBody CreatePostRequestDto cDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 저장되었습니다.", postService.save(userDetails, cDto)), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequestDto uDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 저장되었습니다.", postService.update(id, userDetails, uDto)), HttpStatus.OK);
    }

}
