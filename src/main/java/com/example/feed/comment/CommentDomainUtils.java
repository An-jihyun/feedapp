package com.example.feed.comment;

import com.example.feed.exception.*;
import com.example.feed.post.Post;
import com.example.feed.post.PostRepository;
import com.example.feed.user.User;
import com.example.feed.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentDomainUtils {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //현재 로그인 사용자 조회
    public User getCurrentUser(Long userId) {
        return userRepository.findByIdAndDeletedFalse(userId)
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
        boolean isAuthor    = comment.getUser().getId().equals(currentUserId);
        boolean isPostOwner = comment.getPost().getUser().getId().equals(currentUserId);
        if (!isAuthor && !isPostOwner) {
            throw new UserMismatchException("댓글에 대한 권한이 없습니다.");
        }
        return comment;
    }
}
