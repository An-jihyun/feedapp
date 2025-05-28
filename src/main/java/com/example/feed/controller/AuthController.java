package com.example.feed.controller;

import com.example.feed.dto.logIn.LoginRequestDto;
import com.example.feed.dto.logIn.LoginResponseDto;
import com.example.feed.jwt.JwtTokenProvider;
import com.example.feed.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody LoginRequestDto request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 성공!");
    }
}
