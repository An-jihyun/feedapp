package com.example.feed.comment;

import com.example.feed.comment.dto.CommentResponseDto;
import com.example.feed.comment.dto.CreateCommentRequestDto;
import com.example.feed.comment.dto.UpdateCommentRequestDto;
import com.example.feed.common.dto.ApiResponse;
import com.example.feed.security.userDetail.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> create(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommentResponseDto responseDto = commentService.createComment(postId, requestDto, userDetails.getUserId());
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 댓글이 작성되었습니다.", responseDto), HttpStatus.CREATED);
    }


    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponseDto>>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedAt").descending());
        Page<CommentResponseDto> responseDto = commentService.getCommentsByPost(postId, pageable);
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 댓글이 조회되었습니다.", responseDto), HttpStatus.OK);
    }

    @GetMapping("/users/me/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponseDto>>> getMyComments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedAt").descending());
        Page<CommentResponseDto> responseDto = commentService.getCommentsByUser(userDetails.getUserId(), pageable);
        return new ResponseEntity<>(new ApiResponse<>("내 댓글 목록이 조회되었습니다.", responseDto), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponseDto>>> getCommentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedAt").descending());
        Page<CommentResponseDto> responseDto = commentService.getCommentsByUser(userId, pageable);
        return new ResponseEntity<>(new ApiResponse<>("사용자의 댓글 목록이 조회되었습니다.", responseDto), HttpStatus.OK);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(commentId, dto, userDetails.getUserId());
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 댓글이 수정되었습니다.", null), HttpStatus.OK);
    }


    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUserId());
        return new ResponseEntity<>(new ApiResponse<>("정상적으로 댓글이 삭제되었습니다.", null), HttpStatus.OK);
    }
}

