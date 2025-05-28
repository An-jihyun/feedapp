package com.example.feed.controller;

import com.example.feed.dto.comment.CommentResponseDto;
import com.example.feed.dto.comment.CreateCommentRequestDto;
import com.example.feed.dto.comment.UpdateCommentRequestDto;
import com.example.feed.security.userDetail.CustomUserDetails;
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


    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommentResponseDto responseDto = commentService.createComment(postId, requestDto, userDetails.getUserId());
        return ResponseEntity.status(201).body(responseDto);
    }


    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByPost(
            @PathVariable Long postId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, pageable));
    }


    @GetMapping("/users/me/comments")
    public ResponseEntity<Page<CommentResponseDto>> getMyComments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByUser(userDetails.getUserId(), pageable));
    }

    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByUser(userId, pageable));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> update(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(commentId, dto, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
