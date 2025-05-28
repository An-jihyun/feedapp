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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

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
        Post foundPost = validateUserAccessToPost(id, userDetails);

        //수정 메서드( == setter) -> 더티체킹으로 save() 까지 진행
        foundPost.patchCheck(uDto);

        return PostResponseDto.from(foundPost);
    }

    public PostResponseDto findById(Long id) {
        return PostResponseDto.from(postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시물 id를 확인해주세요.")));
    }

    public void delete(Long id, UserDetails userDetails) {
        Post foundPost = validateUserAccessToPost(id, userDetails);

        postRepository.delete(foundPost);
    }

    /*
    서비스 단에서 사용할 권한 확인 용 메서드 : 반복되는 로직 메서드로 처리
    -> 리팩토링 가능성 : 인가 유틸로 구조 변경뒤 해당 유틸의 메서드로도 활용 가능 (타입변수로 받으면 다른 도메인에서도 사용가능할수도?)
    * */
    private Post validateUserAccessToPost(Long id, UserDetails userDetails) {
        //로그인 한 User 엔티티
        User foundUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("정보와 일치하는 유저가 없습니다."));
        //id 값으로 게시물 조회
        Post foundPost = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시물 id를 확인해주세요."));

        //로그인한 User 가 작성한 포스팅인지 검증 로직(인가)
        if(!foundUser.getId().equals(foundPost.getUser().getId())) {
            throw new UserMismatchException("내가 작성하지 않은 게시물은 삭제할 수 없습니다.");
        }

        return foundPost;
    }

    public Page<PostResponseDto> findPagedPostsByPeriod(Pageable pageable, LocalDateTime periodStart, LocalDateTime periodEnd) {
        //예외?처리 날짜를 설정하지 않았을 시 전체 페이징 데이터 조회
        if(periodStart == null || periodEnd == null) {
            return postRepository.findAll(pageable).map(PostResponseDto::from);
        }
        return postRepository.findByCreatedAtBetween(periodStart, periodEnd, pageable).map(PostResponseDto::from);
    }
}
