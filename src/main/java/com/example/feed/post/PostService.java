package com.example.feed.post;

import com.example.feed.post.dto.request.CreatePostRequestDto;
import com.example.feed.post.dto.request.UpdatePostRequestDto;
import com.example.feed.post.dto.response.PostResponseDto;
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

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostDomainUtils postDomainUtils;

    public PostResponseDto save(CustomUserDetails userDetails, CreatePostRequestDto cDto) {
        /*
        1. 반환타입이 Optional<User> -> user 로 받기 위해선 null 에 대한 명시적 예외처리가 필요함
            -> 예외처리는 가급적 서비스레이어에서 진행하길 희망함
        2. save 메서드는 요구사항에서 원하는 인가단계가 필요하지 않음 validateUserAccessToPost 메서드를 사용하지 않을 것
            -> 요구사항: 수정, 삭제시 내가 작성한 게시물만 가능
         */
        User foundUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("정보와 일치하는 유저가 없습니다."));

        return PostResponseDto.from(postRepository.save(Post.create(cDto.getTitle(), cDto.getContent(), foundUser)));
    }

    @Transactional
    public PostResponseDto update(Long id, CustomUserDetails userDetails, UpdatePostRequestDto uDto) {
        Post foundPost = postDomainUtils.validateUserAccessToPost(id, userDetails);

        //수정 메서드( == setter) -> 더티체킹으로 save() 까지 진행
        foundPost.patchCheck(uDto);

        return PostResponseDto.from(foundPost);
    }

    public PostResponseDto findById(Long id) {
        return PostResponseDto.from(postRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new PostNotFoundException("게시물 id를 확인해주세요.")));
    }

    @Transactional
    public void delete(Long id, CustomUserDetails userDetails) {
        Post foundPost = postDomainUtils.validateUserAccessToPost(id, userDetails);
        foundPost.delete();
    }

    public Page<PostResponseDto> findPagedPostsByPeriod(Pageable pageable, LocalDate periodStart, LocalDate periodEnd) {
        //예외?처리 날짜를 설정하지 않았을 시 전체 페이징 데이터 조회
        if(periodStart == null || periodEnd == null) {
            return postRepository.findAll(pageable).map(PostResponseDto::from);
        }

        //LocalDate -> Time 을 붙여 LocalDateTime 형식으로 바꿔주기
        return postRepository.findByCreatedAtBetween(periodStart.atStartOfDay(), periodEnd.atTime(LocalTime.MAX), pageable).map(PostResponseDto::from);
    }
}
