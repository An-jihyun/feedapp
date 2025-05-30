package com.example.feed.auth;

import com.example.feed.auth.dto.LoginRequestDto;
import com.example.feed.auth.dto.LoginResponseDto;
import com.example.feed.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        authService.logout(token);
        return ResponseEntity.ok("로그아웃 성공!");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody LoginRequestDto request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 성공!");
    }
}
