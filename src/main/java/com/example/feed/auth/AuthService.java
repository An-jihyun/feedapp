package com.example.feed.auth;

import com.example.feed.auth.dto.LoginRequestDto;
import com.example.feed.exception.*;
import com.example.feed.security.TokenBlacklist;
import com.example.feed.security.TokenBlacklistRepository;
import com.example.feed.user.User;
import com.example.feed.security.jwt.JwtTokenProvider;
import com.example.feed.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectDeletedException;
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

    // 로그인 로직
    public String login(String email, String password, HttpServletResponse response) {
        // 이메일/비밀번호로 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        //해당 이멜로 유저찾고 딜리티드값 좃회시 트루면 탈퇴된 회원입니다라는 예외를 던지기!!!!!!!!!!!!!!!!!
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("유저가 존재하지 않습니다."));
        if (user.getDeleted()) {
            throw new UserNotFoundException("탈퇴된 회원입니다.");
                    //커스텀 예외 만들기!!!!!!!!!!!!!!!!!!!!!
        }

        // 인증 성공 시 SecurityContext에 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 발급 (subject로 email 넣음)
        //안지현설명해
        String token = jwtTokenProvider.createToken(email);
        response.setHeader("Authorization", token);
        return token;
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

                request.getUserName(),
                request.getEmail(),
                encodedPassword
        );

        userRepository.save(user);
    }
}
