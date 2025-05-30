package com.example.feed.user;

import com.example.feed.user.dto.DeleteUserRequestDto;
import com.example.feed.user.dto.UserProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원 탈퇴
    public void deleteUser(DeleteUserRequestDto requestDto) {
        User user = userRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 이미 탈퇴한 사용자입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        user.delete();
        userRepository.save(user);
    }
    // 프로필 조회
    public UserProfileResponseDto getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.isDeleted()) {
            throw new IllegalStateException("탈퇴한 사용자입니다.");
        }

        return new UserProfileResponseDto(user.getUserName(), user.getEmail());
    }
}
