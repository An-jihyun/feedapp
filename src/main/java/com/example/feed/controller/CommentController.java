package com.example.feed.controller;

import com.example.feed.dto.comment.CommentResponseDto;
import com.example.feed.dto.comment.CreateCommentRequestDto;
import com.example.feed.dto.comment.UpdateCommentRequestDto;
import com.example.feed.security.CustomUserDetails;
import com.example.feed.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성 (게시글 기준)
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(commentService.createComment(postId, dto, userDetails.getUserId()));
    }

    // 게시글 기준 댓글 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByPost(
            @PathVariable Long postId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, pageable));
    }

    // 댓글 조회 (내 댓글 or 특정 유저 댓글)
    @GetMapping("/comments")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByUser(
            @RequestParam("userId") String userIdParam,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Long userId = userIdParam.equals("me") ? userDetails.getUserId() : Long.parseLong(userIdParam);
        return ResponseEntity.ok(commentService.getCommentsByUser(userId, pageable));
    }

    // 댓글 수정 (내 댓글만 가능)
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> update(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(commentId, dto, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제 (댓글 작성자 또는 게시글 작성자 가능)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
