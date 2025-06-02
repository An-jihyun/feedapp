package com.example.feed.post;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.feed.common.ApiResponse;
import com.example.feed.post.dto.request.CreatePostRequestDto;
import com.example.feed.post.dto.request.UpdatePostRequestDto;
import com.example.feed.post.dto.response.PostResponseDto;
import com.example.feed.post.dto.response.PostWithCommentsResponseDto;
import com.example.feed.security.userDetail.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<ApiResponse<PostResponseDto>> savePost(@Valid @RequestBody CreatePostRequestDto cDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		return new ResponseEntity<>(
			new ApiResponse<>("정상적으로 게시물이 저장되었습니다.", postService.saveNewPost(userDetails, cDto)), HttpStatus.CREATED);
	}

	//update 메서드, 권한확인을 위한 userDetails 객체 포함
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(@PathVariable Long id,
		@Valid @RequestBody UpdatePostRequestDto uDto, @AuthenticationPrincipal CustomUserDetails userDetails) {

		return new ResponseEntity<>(
			new ApiResponse<>("정상적으로 게시물이 저장되었습니다.", postService.updatePostIfAuthor(id, userDetails, uDto)),
			HttpStatus.OK);
	}

	//post 식별자를 사용한 단건 조회
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PostWithCommentsResponseDto>> getPostById(@PathVariable Long id) {
		return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 조회되었습니다.", postService.findById(id)), HttpStatus.OK);
	}

	//post 식별자를 사용한 게시물 삭제, 권한확인을 위한 userDetails 객체 포함
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> softDeletePost(@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		postService.softDeletePostWithOrphans(id, userDetails);
		return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 삭제되었습니다.", null), HttpStatus.OK);
	}

	//게시물 전체 조회 페이지네이션, 검색조건(시작, 종료일) 포함
	@GetMapping
	public ResponseEntity<ApiResponse<Page<PostWithCommentsResponseDto>>> getPagedPostsByPeriod(
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
		@DateTimeFormat(pattern = "yyyyMMdd") LocalDate periodStart,
		@DateTimeFormat(pattern = "yyyyMMdd") LocalDate periodEnd) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedAt").descending());
		return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 조회되었습니다.",
			postService.findPagedPostsPeriodOrAll(pageable, periodStart, periodEnd)), HttpStatus.OK);
	}

	//팔로우한 대상들의 게시물 전체 조회 페이지네이션, 검색조건(시작, 종료일) 포함
	@GetMapping("/follows")
	public ResponseEntity<ApiResponse<Page<PostWithCommentsResponseDto>>> getFollowersPosts(
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
		@DateTimeFormat(pattern = "yyyyMMdd") LocalDate periodStart,
		@DateTimeFormat(pattern = "yyyyMMdd") LocalDate periodEnd,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedAt").descending());
		return new ResponseEntity<>(new ApiResponse<>("정상적으로 게시물이 조회되었습니다.",
			postService.findFollowersPosts(pageable, periodStart, periodEnd, userDetails)), HttpStatus.OK);
	}

}