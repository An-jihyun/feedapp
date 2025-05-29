package com.example.feed.controller;

import com.example.feed.dto.common.ApiResponse;
import com.example.feed.dto.post.request.CreatePostRequestDto;
import com.example.feed.dto.post.request.UpdatePostRequestDto;
import com.example.feed.dto.post.response.PostResponseDto;
import com.example.feed.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> save(@Valid @RequestBody CreatePostRequestDto cDto, @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 저장되었습니다.", postService.save(userDetails, cDto)), HttpStatus.CREATED);
    }

    //update 메서드, 권한확인을 위한 userDetails 객체 포함
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDto>> update(@PathVariable Long id, @Valid @RequestBody UpdatePostRequestDto uDto, @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 저장되었습니다.", postService.update(id, userDetails, uDto)), HttpStatus.OK);
    }

    //post 식별자를 사용한 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDto>> findById(@PathVariable Long id) {
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 조회되었습니다.", postService.findById(id)), HttpStatus.OK);
    }

    //post 식별자를 사용한 게시물 삭제, 권한확인을 위한 userDetails 객체 포함
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        postService.delete(id, userDetails);
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 삭제되었습니다.", null), HttpStatus.NO_CONTENT);
    }

    //게시물 전체 조회 페이지네이션, 검색조건(시작, 종료일) 포함
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponseDto>>> findPagedPostsByPeriod(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @DateTimeFormat(pattern = "yyyyMMdd") LocalDate periodStart, @DateTimeFormat(pattern = "yyyyMMdd") LocalDate periodEnd
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedAt").descending());
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 조회되었습니다.", postService.findPagedPostsByPeriod(pageable, periodStart, periodEnd)), HttpStatus.OK);
    }

}
