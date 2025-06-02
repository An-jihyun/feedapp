package com.example.feed.user;

import com.example.feed.comment.CommentService;
import com.example.feed.follow.FollowService;
import com.example.feed.post.PostService;
import com.example.feed.user.dto.DeleteUserRequestDto;
import com.example.feed.user.dto.UpdatePasswordRequestDto;
import com.example.feed.user.dto.UpdateProfileRequestDto;
import com.example.feed.user.dto.UserProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostService postService;
    private final CommentService commentService;
    private final FollowService followService;

    //회원 탈퇴
    public void deleteUser(DeleteUserRequestDto requestDto) {
        User user = userRepository.findByEmailAndDeletedFalse(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 이미 탈퇴한 사용자입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 게시물 논리적 삭제
        postService.softDeletePostsByUserId(user.getId());

        // 댓글 논리적 삭제
        commentService.softDeleteCommentsByUserId(user.getId());

        //팔로우 논리 삭제
        followService.softDeleteFollowsByUserId(user.getId());

        //유저 논리 삭제
        user.softDelete();
        userRepository.save(user);
    }

    // 프로필 조회
    public UserProfileResponseDto getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.getDeleted()) {
            throw new IllegalStateException("탈퇴한 사용자입니다.");
        }

        return new UserProfileResponseDto(user.getUserName(), user.getEmail());
    }
    // 프로필 수정
    public void updateProfile(Long userId, UpdateProfileRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getDeleted()) {
            throw new IllegalStateException("탈퇴한 사용자는 수정할 수 없습니다.");
        }

        user.updateProfile(dto.getUserName(), dto.getEmail());
        userRepository.save(user);
    }
    // 비밀번호 변경
    public void updatePassword(Long userId, UpdatePasswordRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
            throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
        }

        // 1. 형식 검증
        user.validatePasswordFormat(dto.getNewPassword());

        // 2. 암호화 후 저장
        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.updatePassword(encodedNewPassword);
        userRepository.save(user);
    }
}
