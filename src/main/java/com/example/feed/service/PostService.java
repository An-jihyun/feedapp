package com.example.feed.service;

import com.example.feed.dto.post.request.CreatePostRequestDto;
import com.example.feed.dto.post.response.PostResponseDto;
import com.example.feed.entity.Post;
import com.example.feed.entity.User;
import com.example.feed.exception.UserNotFoundException;
import com.example.feed.repository.PostRepository;
import com.example.feed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponseDto save(String email, CreatePostRequestDto cDto) {
        /*
        로그인이 돼 있는 상태라 예외가 나올일이 없지만 user 로 받기 위해선 예외처리가 필요하긴함
        -> 예외처리는 가급적 서비스레이어에서 진행
         */
        User foundUser = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("email 을 확인해주세요."));

        return PostResponseDto.from(Post.create(cDto.getTitle(), cDto.getContent(), foundUser));
    }

}
