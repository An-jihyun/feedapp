package com.example.feed.post;

import com.example.feed.comment.Comment;
import com.example.feed.comment.CommentRepository;
import com.example.feed.exception.PostNotFoundException;
import com.example.feed.exception.UserMismatchException;
import com.example.feed.exception.UserNotFoundException;
import com.example.feed.follow.Follow;
import com.example.feed.follow.FollowService;
import com.example.feed.follow.FollowsRepository;
import com.example.feed.follow.dto.FollowingListResponseDto;
import com.example.feed.security.userDetail.CustomUserDetails;
import com.example.feed.user.User;
import com.example.feed.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class PostDomainUtils {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final FollowsRepository followsRepository;

    /*
    서비스 단에서 사용할 권한 확인 용 메서드 : 내가 작성한 게시물인지 확인하고 맞으면 해당 게시물을 반환
    서비스에서 private 메서드로 구현했던 메서드지만 각자의 도메인에서 반복 사용되는 기능들을 Domain 별 Utils 로 분리해서 리팩토링 해보는 것이 목표
    */
    public Post validateUserAccessToPost(Long id, CustomUserDetails userDetails) {
        //로그인 한 User 엔티티
        User foundUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("정보와 일치하는 유저가 없습니다."));
        //id 값으로 게시물 조회
        Post foundPost = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("해당 식별자와 일치하는 게시물이 없습니다."));

        //로그인한 User 가 작성한 포스팅인지 검증 로직(인가)
        if (!foundUser.getId().equals(foundPost.getUser().getId())) {
            throw new UserMismatchException("내가 작성하지 않은 게시물입니다.");
        }

        return foundPost;
    }

    //현재 로그인한 유저의 팔로잉한 대상들의 id 값을 리스트로 반환
    public List<Long> getFollowingUserIds(Long userId) {
        return followsRepository.findAllByFollowerIdAndDeletedFalse(userId).stream().map(follow -> follow.getFollowing().getId()).toList();
    }

    //Collectors.groupingBy 를 통해 postId 기준으로 정렬된 콘텐츠리스트 를 가진 Map 을 만듦
    public Map<Long, List<String>> commentsByPostId() {
        return commentRepository.findAllByDeletedFalse().stream().collect(Collectors.groupingBy(comment -> comment.getPost().getId(), Collectors.mapping(Comment::getContent, Collectors.toList())));
    }

    /*
    이하 옵셔널 객체를 꺼내오는 메서드 null 일 경우 반환값과 일치하는 예외 Throw
     */
    public User getUserOrThrow(String email) {
        return userRepository.findByEmailAndDeletedFalse(email).orElseThrow(() -> new UserNotFoundException("해당 이메일과 일치하는 사용자가 없습니다."));
    }

    public Post getPostOrThrow(Long id) {
        return postRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new PostNotFoundException("해당 식별자와 일치하는 게시물이 없습니다."));
    }

}
