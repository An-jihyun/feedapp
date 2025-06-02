package com.example.feed.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.feed.user.dto.DeleteUserRequestDto;
import com.example.feed.user.dto.UpdatePasswordRequestDto;
import com.example.feed.user.dto.UpdateProfileRequestDto;
import com.example.feed.user.dto.UserProfileResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	// 회원 탈퇴
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteUser(@RequestBody DeleteUserRequestDto request) {
		userService.deleteUser(request);
		return ResponseEntity.ok("회원 탈퇴 완료");
	}

	// 프로필 조회
	@GetMapping("/{id}")
	public ResponseEntity<UserProfileResponseDto> getUserProfile(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserProfile(id));
	}

	// 프로필 수정
	@PutMapping("/me")
	public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequestDto requestDto) {
		Long userId = 1L; // 인증 붙이면 교체
		userService.updateProfile(userId, requestDto);
		return ResponseEntity.ok("프로필 수정 완료");
	}

	// 비밀 번호 변경
	@PutMapping("/me/password")
	public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequestDto requestDto) {
		Long userId = 1L;
		userService.updatePassword(userId, requestDto);
		return ResponseEntity.ok("비밀번호 변경 완료");
	}

}
