package com.example.feed.user;

import com.example.feed.user.dto.DeleteUserRequestDto;
import com.example.feed.user.dto.UserProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody DeleteUserRequestDto request) {
        userService.deleteUser(request);
        return ResponseEntity.ok("회원탈퇴 완료");
    }

    // 프로필 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }
}
