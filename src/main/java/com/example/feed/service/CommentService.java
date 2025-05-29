package com.example.feed.service;

import com.example.feed.dto.comment.CommentResponseDto;
import com.example.feed.dto.comment.CreateCommentRequestDto;
import com.example.feed.dto.comment.UpdateCommentRequestDto;
import com.example.feed.entity.Comment;
import com.example.feed.entity.Post;
import com.example.feed.entity.User;
import com.example.feed.exception.CommentNotFoundException;
import com.example.feed.exception.PostNotFoundException;
import com.example.feed.exception.UserMismatchException;
import com.example.feed.exception.UserNotFoundException;
import com.example.feed.repository.CommentRepository;
import com.example.feed.repository.PostRepository;
import com.example.feed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentResponseDto createComment(Long postId, CreateCommentRequestDto requestDto, Long userId) {
        User user = findUserById(userId);
        Post post = findPostById(postId);

        Comment comment = Comment.create(requestDto.getContent(), user, post);
        return CommentResponseDto.from(commentRepository.save(comment));
    }

    public Page<CommentResponseDto> getCommentsByPost(Long postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);
        return comments.map(CommentResponseDto::from);
    }

    public Page<CommentResponseDto> getCommentsByUser(Long userId,Pageable pageable) {
        Page<Comment> comments = commentRepository.findByUserId(userId,pageable);
        return comments.map(CommentResponseDto::from);
    }

    public void updateComment(Long commentId, UpdateCommentRequestDto requestDto, Long userId) {
        Comment comment = findCommentById(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new UserMismatchException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }
        comment.updateContent(requestDto.getContent());
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = findCommentById(commentId);
        if (!comment.getUser().getId().equals(userId)
                && !comment.getPost().getUser().getId().equals(userId)) {
            throw new UserMismatchException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }




    public void deleteCommentsByPostId(Long postId) {
        commentRepository.deleteAllByPostId(postId);
    }

    public void deleteCommentsByUserId(Long userId) {
        commentRepository.deleteAllByUserId(userId);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글이 존재하지 않습니다."));
    }
}

