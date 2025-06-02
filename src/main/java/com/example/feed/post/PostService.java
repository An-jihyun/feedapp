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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final FollowsRepository followsRepository;

    private final CommentService commentService;
    private final LikeService likeService;

    public PostResponseDto save(CustomUserDetails userDetails, CreatePostRequestDto cDto) {
        //로그인 한 User 엔티티
        User foundUser = getUserOrThrow(userDetails.getUsername());

        //로그인 한 User 정보가 담긴 게시물 생성
        Post savePost = Post.create(cDto.getTitle(), cDto.getContent(), foundUser);

        //리포지터리 저장메서드 호출 반환값 저장된 엔티티
        Post savedPost = postRepository.save(savePost);

        //응답객체로 변환해서 반환
        return PostResponseDto.from(savedPost);
    }

    public PostResponseDto update(Long id, CustomUserDetails userDetails, UpdatePostRequestDto uDto) {
        //로그인 한 User 엔티티
        User foundUser = getUserOrThrow(userDetails.getUsername());

        //수정할 게시물 id 값으로 검색
        Post foundPost = getPostOrThrow(id);

        //검증메서드 내가 작성한 게시물이 아닐시 Throw
        validatePostOwnerOrThrow(foundUser, foundPost);

        //수정 메서드( == setter) -> 더티체킹으로 save() 까지 진행
        foundPost.patchIfNotNull(uDto.getTitle(), uDto.getContent());

        return PostResponseDto.from(foundPost);
    }

    @Transactional(readOnly = true)
    public PostWithCommentsResponseDto findById(Long id) {
        return PostWithCommentsResponseDto.from(getPostOrThrow(id), commentRepository.findByPostIdAndDeletedFalse(id).stream().map(Comment::getContent).toList(),
                likeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, id));
    }

    public void softDelete(Long id, CustomUserDetails userDetails) {
        //로그인 한 User 엔티티
        User foundUser = getUserOrThrow(userDetails.getUsername());

        //수정할 게시물 id 값으로 검색
        Post foundPost = getPostOrThrow(id);

        //검증메서드 내가 작성한 게시물이 아닐시 Throw
        validatePostOwnerOrThrow(foundUser, foundPost);

        //논리적 삭제 전 고아객체들을 먼저 삭제 comments, like
        commentService.softDeleteCommentsByPostId(foundPost.getId());
        likeService.deleteAllLikesByTargetId(LikeTargetType.POST, foundPost.getId());
        foundPost.softDelete();
    }

    @Transactional(readOnly = true)
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
            List<String> contents = commentsByPostId().getOrDefault(post.getId(), Collections.emptyList());
            return PostWithCommentsResponseDto.from(post, contents, likeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, post.getId()));
        });
    }

    @Transactional(readOnly = true)
    public Page<PostWithCommentsResponseDto> findFollowersPosts(Pageable pageable, LocalDate periodStart, LocalDate periodEnd, CustomUserDetails userDetails) {
        //로그인 한 User 엔티티
        User foundUser = getUserOrThrow(userDetails.getUsername());

        //팔로잉한 대상이 없을 경우 빈 페이지객체 반환
        List<Long> followingUserIds = getFollowingUserIds(foundUser.getId());
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
            List<String> contents = commentsByPostId().getOrDefault(post.getId(), Collections.emptyList());
            return PostWithCommentsResponseDto.from(post, contents, likeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, post.getId()));
        });
    }

    //사용자 논리적 삭제시 해당 메서드도 같이 사용해주면 작성자의 모든 Post 를 논리적삭제시킴
    @Transactional
    public void softDeletePostsByUserId(Long userId) {
        postRepository.findAllByUserIdAndDeletedFalse(userId).forEach(Post::softDelete);
    }

    //권한 확인 용 메서드 : 로그인한 유저의 아이디값과 검증이 필요한 메서드의 게시물 작성자가 일치하는지 확인
    private void validatePostOwnerOrThrow(User foundUser, Post foundPost) {
        //로그인한 User 가 작성한 포스팅인지 검증 로직(인가)
        if (!foundUser.getId().equals(foundPost.getUser().getId())) {
            throw new UserMismatchException("내가 작성하지 않은 게시물입니다.");
        }
    }

    //현재 로그인한 유저의 팔로잉한 대상들의 id 값을 리스트로 반환
    private List<Long> getFollowingUserIds(Long userId) {
        return followsRepository.findAllByFollowerIdAndDeletedFalse(userId).stream().map(follow -> follow.getFollowing().getId()).toList();
    }

    //Collectors.groupingBy 를 통해 postId 기준으로 결합된 콘텐츠리스트 를 가진 Map 을 반환
    private Map<Long, List<String>> commentsByPostId() {
        return commentRepository.findAllByDeletedFalse().stream().collect(Collectors.groupingBy(comment -> comment.getPost().getId(), Collectors.mapping(Comment::getContent, Collectors.toList())));
    }

    //이하 옵셔널 객체를 꺼내오는 메서드 null 일 경우 반환값과 일치하는 예외 Throw
    private User getUserOrThrow(String email) {
        return userRepository.findByEmailAndDeletedFalse(email).orElseThrow(() -> new UserNotFoundException("해당 이메일과 일치하는 사용자가 없습니다."));
    }

    private Post getPostOrThrow(Long id) {
        return postRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new PostNotFoundException("해당 식별자와 일치하는 게시물이 없습니다."));
    }
}
