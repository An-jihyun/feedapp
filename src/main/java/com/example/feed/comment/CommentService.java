package com.example.feed.comment;

import com.example.feed.comment.dto.CommentResponseDto;
import com.example.feed.comment.dto.CreateCommentRequestDto;
import com.example.feed.comment.dto.UpdateCommentRequestDto;
import com.example.feed.like.LikeService;
import com.example.feed.like.LikeTargetType;
import com.example.feed.post.Post;
import com.example.feed.user.User;
import com.example.feed.exception.CommentNotFoundException;
import com.example.feed.exception.PostNotFoundException;
import com.example.feed.exception.UserMismatchException;
import com.example.feed.exception.UserNotFoundException;
import com.example.feed.post.PostRepository;
import com.example.feed.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentDomainUtils commentDomainUtils;
    private final LikeService likeService;


    public CommentResponseDto createComment(Long postId, CreateCommentRequestDto requestDto, String email) {
        User user = commentDomainUtils.getCurrentUser(email);
        Post post = commentDomainUtils.getPost(postId);
        Comment comment = Comment.create(requestDto.getContent(), user, post);
        return CommentResponseDto.from(commentRepository.save(comment));
    }

    @Transactional(readOnly=true)
    public Page<CommentResponseDto> getCommentsByPost(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdAndDeletedFalse(postId, pageable)
                .map(CommentResponseDto::from);
    }

    @Transactional(readOnly=true)
    public Page<CommentResponseDto> getCommentsByUser(Long userId,Pageable pageable) {
        return commentRepository.findByUserIdAndDeletedFalse(userId, pageable)
                .map(CommentResponseDto::from);
    }


    public void updateComment(Long commentId, UpdateCommentRequestDto requestDto, Long userId) {
        commentDomainUtils.validateAndGetComment(commentId, userId).updateContent(requestDto.getContent());
    }


    public void deleteComment(Long commentId, Long userId) {
        likeService.deleteAllLikesByTargetId(LikeTargetType.COMMENT, commentId);
        commentDomainUtils.validateAndGetComment(commentId, userId).softDelete();
    }

    //상위 도메인 호출 용(soft delete)
    public void softDeleteCommentsByPostId(Long postId) {
        commentRepository.findByPostIdAndDeletedFalse(postId).forEach(comment -> {
            likeService.deleteAllLikesByTargetId(LikeTargetType.COMMENT, comment.getId()); // 좋아요 먼저 삭제
            comment.softDelete(); // 댓글 삭제
        });
    }
    public void softDeleteCommentsByUserId(Long userId) {
        commentRepository.findByUserIdAndDeletedFalse(userId).forEach(comment -> {
            likeService.deleteAllLikesByTargetId(LikeTargetType.COMMENT, comment.getId()); // 좋아요 먼저 삭제
            comment.softDelete(); // 댓글 삭제
        });
    }



}

