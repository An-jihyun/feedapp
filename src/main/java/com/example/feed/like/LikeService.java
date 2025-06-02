package com.example.feed.like;

import com.example.feed.comment.Comment;
import com.example.feed.comment.CommentRepository;
import com.example.feed.exception.CommentNotFoundException;
import com.example.feed.exception.PostNotFoundException;
import com.example.feed.exception.SelfLikeNotAllowedException;
import com.example.feed.exception.UserNotFoundException;
import com.example.feed.like.dto.LikeResponseDto;
import com.example.feed.post.Post;
import com.example.feed.post.PostRepository;
import com.example.feed.user.User;
import com.example.feed.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public LikeResponseDto toggleLike(LikeTargetType targetType, Long targetId, Long userId) {
        User user = findUserById(userId);

        // 본인 작성 여부 검증
        validateNotSelfContent(targetType, targetId, userId);

        // 기존 좋아요 여부 확인 및 토글 처리
        boolean wasLiked = likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);

        if (wasLiked) {
            removeLike(userId, targetType, targetId);
        } else {
            addLike(user, targetType, targetId);
        }

        // 현재 상태 반환
        return buildLikeResponse(targetType, targetId, userId);
    }

    @Transactional
    public LikeResponseDto getLikeStatus(LikeTargetType targetType, Long targetId, Long userId) {
        validateTargetExists(targetType, targetId);
        return buildLikeResponse(targetType, targetId, userId);
    }

    public void deleteAllLikesByTargetId(LikeTargetType targetType, Long targetId) {
        likeRepository.deleteAllByTargetTypeAndTargetId(targetType, targetId);
    }

    public void deleteAllLikesByUserId(Long userId) {
        likeRepository.deleteAllByUserId(userId);
    }


    // 헬퍼 메서드
    private void addLike(User user, LikeTargetType targetType, Long targetId) {
        Like newLike = Like.create(user, targetType, targetId);
        likeRepository.save(newLike);
    }

    private void removeLike(Long userId, LikeTargetType targetType, Long targetId) {
        Like existingLike = likeRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                .orElseThrow(() -> new RuntimeException("좋아요 정보를 찾을 수 없습니다."));
        likeRepository.delete(existingLike);
    }

    private LikeResponseDto buildLikeResponse(LikeTargetType targetType, Long targetId, Long userId) {
        long likeCount = likeRepository.countByTargetTypeAndTargetId(targetType, targetId);
        boolean isLiked = likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        return LikeResponseDto.of(targetType, targetId, likeCount, isLiked);
    }

    private void validateNotSelfContent(LikeTargetType targetType, Long targetId, Long userId) {
        switch (targetType) {
            case POST:
                Post post = findPostById(targetId);
                if (post.getUser().getId().equals(userId)) {
                    throw new SelfLikeNotAllowedException("본인이 작성한 게시물에는 좋아요를 누를 수 없습니다.");
                }
                break;
            case COMMENT:
                Comment comment = findCommentById(targetId);
                if (comment.getUser().getId().equals(userId)) {
                    throw new SelfLikeNotAllowedException("본인이 작성한 댓글에는 좋아요를 누를 수 없습니다.");
                }
                break;
        }
    }

    private void validateTargetExists(LikeTargetType targetType, Long targetId) {
        switch (targetType) {
            case POST:
                findPostById(targetId);
                break;
            case COMMENT:
                findCommentById(targetId);
                break;
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Post findPostById(Long postId) {
        return postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new PostNotFoundException("게시물을 찾을 수 없습니다."));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
    }
}