package com.example.feed.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.feed.comment.dto.CommentResponseDto;
import com.example.feed.comment.dto.CreateCommentRequestDto;
import com.example.feed.comment.dto.UpdateCommentRequestDto;
import com.example.feed.exception.CommentNotFoundException;
import com.example.feed.exception.PostNotFoundException;
import com.example.feed.exception.UserMismatchException;
import com.example.feed.exception.UserNotFoundException;
import com.example.feed.like.LikeRepository;
import com.example.feed.like.LikeService;
import com.example.feed.like.LikeTargetType;
import com.example.feed.post.Post;
import com.example.feed.post.PostRepository;
import com.example.feed.user.User;
import com.example.feed.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

	private final CommentRepository commentRepository;
	private final LikeRepository likeRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final LikeService likeService;

	public CommentResponseDto saveComment(Long postId, CreateCommentRequestDto requestDto, String email) {
		User user = getCurrentUser(email);
		Post post = getPost(postId);
		Comment comment = Comment.create(requestDto.getContent(), user, post);
		//좋아요 수는 초기에 0으로 설정
		return CommentResponseDto.from(commentRepository.save(comment));
	}

	@Transactional(readOnly = true)
	public Page<CommentResponseDto> readCommentsByPost(Long postId, Pageable pageable) {
		return commentRepository
			.findByPostIdAndDeletedFalse(postId, pageable)
			.map(comment -> CommentResponseDto.from(
				comment,
				likeRepository.countByTargetTypeAndTargetId(
					LikeTargetType.COMMENT, comment.getId())
			));
	}

	@Transactional(readOnly = true)
	public Page<CommentResponseDto> readCommentsByUser(Long userId, Pageable pageable) {
		return commentRepository
			.findByUserIdAndDeletedFalse(userId, pageable)
			.map(comment -> CommentResponseDto.from(
				comment,
				likeRepository.countByTargetTypeAndTargetId(
					LikeTargetType.COMMENT, comment.getId())
			));
	}

	public CommentResponseDto updateCommentContent(Long commentId, UpdateCommentRequestDto requestDto, Long userId) {
		Comment comment = validateAndGetComment(commentId, userId);
		comment.updateContent(requestDto.getContent());
		long likeCount = likeRepository.countByTargetTypeAndTargetId(LikeTargetType.COMMENT, comment.getId());
		return CommentResponseDto.from(comment, likeCount);
	}

	public void removeComment(Long commentId, Long userId) {
		likeService.deleteAllLikesByTargetId(LikeTargetType.COMMENT, commentId);
		validateAndGetComment(commentId, userId).softDelete();
	}

	// ================= 헬퍼 메서드 =================

	//현재 로그인 사용자 조회
	public User getCurrentUser(String email) {
		return userRepository.findByEmailAndDeletedFalse(email)
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
	}

	//댓글 단건 조회
	public Comment getComment(Long commentId) {
		return commentRepository.findByIdAndDeletedFalse(commentId)
			.orElseThrow(() -> new CommentNotFoundException("댓글이 존재하지 않습니다."));
	}

	//게시글 조회
	public Post getPost(Long postId) {
		return postRepository.findByIdAndDeletedFalse(postId)
			.orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
	}

	/* 댓글 수정·삭제 권한 검증 후 Comment 반환
	댓글 작성자이거나, 댓글이 달린 게시글의 작성자**/
	public Comment validateAndGetComment(Long commentId, Long currentUserId) {
		Comment comment = getComment(commentId);
		boolean isAuthor = comment.getUser().getId().equals(currentUserId);
		boolean isPostOwner = comment.getPost().getUser().getId().equals(currentUserId);
		if (!isAuthor && !isPostOwner) {
			throw new UserMismatchException("댓글에 대한 권한이 없습니다.");
		}
		return comment;
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