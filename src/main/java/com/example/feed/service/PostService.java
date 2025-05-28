package com.example.feed.service;

import com.example.feed.dto.post.request.CreatePostRequestDto;
import com.example.feed.dto.post.request.UpdatePostRequestDto;
import com.example.feed.dto.post.response.PostResponseDto;
import com.example.feed.entity.Post;
import com.example.feed.entity.User;
import com.example.feed.exception.PostNotFoundException;
import com.example.feed.exception.UserMismatchException;
import com.example.feed.exception.UserNotFoundException;
import com.example.feed.repository.PostRepository;
import com.example.feed.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponseDto save(UserDetails userDetails, CreatePostRequestDto cDto) {
        /*
        로그인이 돼 있는 상태라 예외가 나올일이 없지만 user 로 받기 위해선 예외처리가 필요하긴함
        -> 예외처리는 가급적 서비스레이어에서 진행
         */
        User foundUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("정보와 일치하는 유저가 없습니다."));

        return PostResponseDto.from(postRepository.save(Post.create(cDto.getTitle(), cDto.getContent(), foundUser)));
    }

    @Transactional
    public PostResponseDto update(Long id, UserDetails userDetails, UpdatePostRequestDto uDto) {
        //로그인 한 User 엔티티
        User foundUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("정보와 일치하는 유저가 없습니다."));
        Post foundPost = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시물 id를 확인해주세요."));

        //로그인한 User 가 작성한 포스팅인지 검증 로직(인가)
        if(!foundUser.getId().equals(foundPost.getUser().getId())) {
            throw new UserMismatchException("내가 작성하지 않은 게시물은 수정할 수 없습니다.");
        }

        //수정 메서드( == setter) -> 더티체킹으로 save() 까지 진행
        foundPost.patchCheck(uDto);

        return PostResponseDto.from(foundPost);
    }
}
