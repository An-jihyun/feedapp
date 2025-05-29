package com.example.feed.post;

import com.example.feed.comment.Comment;
import com.example.feed.comment.CommentRepository;
import com.example.feed.comment.dto.CommentSimpleResponseDto;
import com.example.feed.post.dto.request.CreatePostRequestDto;
import com.example.feed.post.dto.request.UpdatePostRequestDto;
import com.example.feed.post.dto.response.PostResponseDto;
import com.example.feed.user.User;
import com.example.feed.exception.PostNotFoundException;
import com.example.feed.exception.UserMismatchException;
import com.example.feed.exception.UserNotFoundException;
import com.example.feed.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostResponseDto save(UserDetails userDetails, CreatePostRequestDto cDto) {
        User foundUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("정보와 일치하는 유저가 없습니다."));
        return PostResponseDto.from(postRepository.save(Post.create(cDto.getTitle(), cDto.getContent(), foundUser)));
    }

    @Transactional
    public PostResponseDto update(Long id, UserDetails userDetails, UpdatePostRequestDto uDto) {
        Post foundPost = validateUserAccessToPost(id, userDetails);
        foundPost.patchCheck(uDto);
        return PostResponseDto.from(foundPost);
    }

    public PostResponseDto findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시물 id를 확인해주세요."));

        List<Comment> comments = commentRepository.findByPostId(id);
        List<CommentSimpleResponseDto> commentDtos = comments.stream()
                .map(CommentSimpleResponseDto::from)
                .toList();

        return PostResponseDto.from(post, commentDtos);
    }

    public void delete(Long id, UserDetails userDetails) {
        Post foundPost = validateUserAccessToPost(id, userDetails);
        commentRepository.deleteAllByPostId(id); // 댓글 먼저 삭제
        postRepository.delete(foundPost);
    }

    private Post validateUserAccessToPost(Long id, UserDetails userDetails) {
        User foundUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("정보와 일치하는 유저가 없습니다."));
        Post foundPost = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시물 id를 확인해주세요."));

        if (!foundUser.getId().equals(foundPost.getUser().getId())) {
            throw new UserMismatchException("내가 작성하지 않은 게시물은 삭제할 수 없습니다.");
        }

        return foundPost;
    }

    public Page<PostResponseDto> findPagedPostsByPeriod(Pageable pageable, LocalDate periodStart, LocalDate periodEnd) {
        if (periodStart == null || periodEnd == null) {
            return postRepository.findAll(pageable).map(PostResponseDto::from);
        }

        return postRepository.findByCreatedAtBetween(
                periodStart.atStartOfDay(),
                periodEnd.atTime(LocalTime.MAX),
                pageable
        ).map(PostResponseDto::from);
    }
}
