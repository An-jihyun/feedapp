package com.example.feed.post;

import com.example.feed.comment.Comment;
import com.example.feed.comment.CommentRepository;
import com.example.feed.comment.CommentService;
import com.example.feed.follow.FollowsRepository;
import com.example.feed.like.LikeRepository;
import com.example.feed.like.LikeService;
import com.example.feed.like.LikeTargetType;
import com.example.feed.post.dto.request.CreatePostRequestDto;
import com.example.feed.post.dto.request.UpdatePostRequestDto;
import com.example.feed.post.dto.response.PostResponseDto;
import com.example.feed.post.dto.response.PostWithCommentsResponseDto;
import com.example.feed.security.userDetail.CustomUserDetails;
import com.example.feed.user.User;
import com.example.feed.exception.PostNotFoundException;
import com.example.feed.exception.UserMismatchException;
import com.example.feed.exception.UserNotFoundException;
import com.example.feed.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostDomainUtils postDomainUtils;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final LikeRepository likeRepository;

    public PostResponseDto save(CustomUserDetails userDetails, CreatePostRequestDto cDto) {
        /*
        1. 반환타입이 Optional<User> -> user 로 받기 위해선 null 에 대한 명시적 예외처리가 필요함
            -> 예외처리는 가급적 서비스레이어에서 진행하길 희망함
        2. save 메서드는 요구사항에서 원하는 인가단계가 필요하지 않음 validateUserAccessToPost 메서드를 사용하지 않을 것
            -> 요구사항: 수정, 삭제시 내가 작성한 게시물만 가능
         */
        User foundUser = postDomainUtils.getUserOrThrow(userDetails.getUsername());

        return PostResponseDto.from(postRepository.save(Post.create(cDto.getTitle(), cDto.getContent(), foundUser)));
    }

    @Transactional
    public PostResponseDto update(Long id, CustomUserDetails userDetails, UpdatePostRequestDto uDto) {
        Post foundPost = postDomainUtils.validateUserAccessToPost(id, userDetails);

        //수정 메서드( == setter) -> 더티체킹으로 save() 까지 진행
        foundPost.patchIfNotNull(uDto.getTitle(), uDto.getContent());

        return PostResponseDto.from(foundPost);
    }

    public PostWithCommentsResponseDto findById(Long id) {
        return PostWithCommentsResponseDto.from(postDomainUtils.getPostOrThrow(id), commentRepository.findByPostIdAndDeletedFalse(id).stream().map(Comment::getContent).toList(),
                likeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, id));
    }

    @Transactional
    public void softDelete(Long id, CustomUserDetails userDetails) {
        Post foundPost = postDomainUtils.validateUserAccessToPost(id, userDetails);
        //논리적 삭제 전 고아객체들을 먼저 삭제 comments, like
        commentService.softDeleteCommentsByPostId(foundPost.getId());
        likeService.deleteAllLikesByTargetId(LikeTargetType.POST, foundPost.getId());
        foundPost.softDelete();
    }

    public Page<PostWithCommentsResponseDto> findPagedPostsPeriodOrAll(Pageable pageable, LocalDate periodStart, LocalDate periodEnd) {
        //넘겨받은 매개변수에 따라 페이징 데이터가 다르기에 빈 페이징객체를 먼저 선언
        Page<Post> posts;
        //날짜 관련 데이터 확인 로직 없을시 pageable 만을 통한 조회
        if(periodStart == null || periodEnd == null) {
            posts = postRepository.findAllByDeletedFalse(pageable);
        } else {
            //LocalDate -> Time 을 붙여 LocalDateTime 형식으로 바꿔주기
            posts = postRepository.findByCreatedAtBetweenAndDeletedFalse(periodStart.atStartOfDay(), periodEnd.atTime(LocalTime.MAX), pageable);
        }

        //응답 객체로 변환해서 반환
        return posts.map(post -> {
            List<String> contents = postDomainUtils.commentsByPostId().getOrDefault(post.getId(), Collections.emptyList());
            return PostWithCommentsResponseDto.from(post, contents, likeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, post.getId()));
        });
    }

    public Page<PostWithCommentsResponseDto> findFollowersPosts(Pageable pageable, LocalDate periodStart, LocalDate periodEnd, CustomUserDetails userDetails) {
        User foundUser = postDomainUtils.getUserOrThrow(userDetails.getUsername());
        //팔로잉한 대상이 없을 경우 빈 페이지객체 반환
        List<Long> followingUserIds = postDomainUtils.getFollowingUserIds(foundUser.getId());
        if(followingUserIds.isEmpty()) {
            return Page.empty();
        }

        //넘겨받은 매개변수에 따라 페이징 데이터가 다르기에 빈 페이징객체를 먼저 선언
        Page<Post> posts;
        //날짜 관련 데이터 확인 로직 없을시 pageable 만을 통한 조회
        if(periodStart == null || periodEnd == null) {
            posts = postRepository.findByUserIdInAndDeletedFalse(followingUserIds, pageable);
        } else {
            //LocalDate -> Time 을 붙여 LocalDateTime 형식으로 바꿔주기
            posts = postRepository.findByUserIdInAndCreatedAtBetweenAndDeletedFalse(followingUserIds, periodStart.atStartOfDay(), periodEnd.atTime(LocalTime.MAX), pageable);
        }

        //응답 객체로 변환해서 반환
        return posts.map(post -> {
            List<String> contents = postDomainUtils.commentsByPostId().getOrDefault(post.getId(), Collections.emptyList());
            return PostWithCommentsResponseDto.from(post, contents, likeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, post.getId()));
        });
    }

    //사용자 논리적 삭제시 해당 메서드도 같이 사용해주면 작성자의 모든 Post 를 논리적삭제시킴
    @Transactional
    public void softDeletePostsByUserId(Long userId) {
        postRepository.findAllByUserIdAndDeletedFalse(userId).forEach(Post::softDelete);
    }
}
