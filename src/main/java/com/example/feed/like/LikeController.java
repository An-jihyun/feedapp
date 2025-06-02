package com.example.feed.like;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.feed.common.ApiResponse;
import com.example.feed.like.dto.LikeResponseDto;
import com.example.feed.security.userDetail.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

	private final LikeService likeService;

	@PostMapping("/{targetType}/{targetId}")
	public ResponseEntity<ApiResponse<LikeResponseDto>> toggleLike(
		@PathVariable LikeTargetType targetType,
		@PathVariable Long targetId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		LikeResponseDto responseDto = likeService.toggleLike(targetType, targetId, userDetails.getUserId());
		return new ResponseEntity<>(new ApiResponse<>("좋아요 처리가 완료되었습니다.", responseDto), HttpStatus.OK);
	}

	@GetMapping("/{targetType}/{targetId}")
	public ResponseEntity<ApiResponse<LikeResponseDto>> getLikeStatus(
		@PathVariable LikeTargetType targetType,
		@PathVariable Long targetId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		LikeResponseDto responseDto = likeService.getLikeStatus(targetType, targetId, userDetails.getUserId());
		return new ResponseEntity<>(new ApiResponse<>("좋아요 정보가 조회되었습니다.", responseDto), HttpStatus.OK);
	}

}