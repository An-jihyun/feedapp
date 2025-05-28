package com.example.feed.service;

import com.example.feed.dto.logIn.LoginRequestDto;
import com.example.feed.entity.TokenBlacklist;
import com.example.feed.entity.User;
import com.example.feed.exception.CustomUnauthorizedException;
import com.example.feed.jwt.JwtTokenProvider;
import com.example.feed.repository.TokenBlacklistRepository;
import com.example.feed.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenBlacklistRepository blacklistRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public String login(String email, String password) {
        // 이메일/비밀번호로 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // 인증 성공 시 SecurityContext에 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 발급 (subject로 email 넣음)
        return jwtTokenProvider.createToken(email);
    }

    // 로그아웃 로직 추가
    @Transactional
    public void logout(String token) {
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new CustomUnauthorizedException("Invalid or missing token");
        }
        LocalDateTime expiration = jwtTokenProvider.getExpiration(token);
        TokenBlacklist blacklist = new TokenBlacklist(token, expiration);
        blacklistRepository.save(blacklist);
    }

    //회원 가입 로직 추가
    public void signup(LoginRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                null,
                request.getUserName(),
                request.getEmail(),
                encodedPassword
        );

        userRepository.save(user);
    }
}
