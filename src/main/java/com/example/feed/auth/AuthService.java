package com.example.feed.auth;

import com.example.feed.auth.dto.LoginRequestDto;
import com.example.feed.security.TokenBlacklist;
import com.example.feed.security.TokenBlacklistRepository;
import com.example.feed.user.User;
import com.example.feed.exception.EmailAlreadyExistsException;
import com.example.feed.exception.InvalidTokenException;
import com.example.feed.exception.TokenAlreadyBlacklistedException;
import com.example.feed.exception.TokenRequiredException;
import com.example.feed.security.jwt.JwtTokenProvider;
import com.example.feed.user.UserRepository;
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

        // 1. 토큰 null/empty 체크
        if (token == null || token.trim().isEmpty()) {
            throw new TokenRequiredException("로그아웃을 위한 토큰이 필요합니다.");
        }

        // 2. 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }

        // 3. 이미 블랙리스트에 있는지 확인
        if (blacklistRepository.existsByToken(token)) {
            throw new TokenAlreadyBlacklistedException("이미 로그아웃된 토큰입니다.");
        }

        LocalDateTime expiration = jwtTokenProvider.getExpiration(token);
        TokenBlacklist blacklist = new TokenBlacklist(token, expiration);
        blacklistRepository.save(blacklist);
    }

    //회원 가입 로직 추가
    public void signup(LoginRequestDto request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다: " + request.getEmail());
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
