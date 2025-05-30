package com.example.feed.security.jwt;

import com.example.feed.security.userDetail.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // 이 키는 JWT 서명을 생성하고 검증할 때 사용
    @Value("${jwt.secret}")
    private String secretKey;

    // JWT의 만료시간을 설정하는 값
    @Value("${jwt.expiration-in-ms}")
    private long validityInMilliseconds;

    // JWT 서명을 위한 암호화 키 객체
    private Key key;

    private final CustomUserDetailsService customUserDetailsService;

    // secretKey를 Key 객체로 변환, @PostConstruct는 객체 생성 후 자동으로 이 메서드를 실행하게 한다.
    // HMAC-SHA256 방식에서 서명 키로 사용할 Key를 준비
    @PostConstruct
    public void init() {
        // Base64 디코딩 추가
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    // JWT 토큰 생성
    public String createToken(String subject) {

        // 현재 시각을 가져오고, 만료시간을 현재 시간 + 설정값만큼 계산
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        // subject는 일반적으로 이메일 또는 사용자 ID로 JWT에 저장되는 주요 정보
        // 발급 시간과 만료 시간도 설정
        // HS256 알고리즘을 사용해 key로 서명
        // 마지막에 .compact()로 문자열 형태의 토큰으로 변환
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    // 토큰에서 subject 추출
    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {

        // JWT 토큰에서 사용자 이메일(subject) 추출
        String email = getSubject(token);

        // 이메일로 CustomUserDetailsService 에서 사용자 정보 조회
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // UsernamePasswordAuthenticationToken 객체 생성
        // 두 번째 파라미터는 credential(비밀번호) 자리에 빈 문자열("") 넣음 (이미 인증된 상태니까)
        // 세 번째 파라미터는 사용자의 권한 목록
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public LocalDateTime getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)  // 수정된 부분
                .build()
                .parseClaimsJws(token)
                .getBody();
        Date expiration = claims.getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // HTTP 요청 헤더 'Authorization' 에서 Bearer 토큰 추출 메서드
    public String resolveToken(HttpServletRequest request) {

        // Authorization 헤더 값 읽기 (예: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...")
        String bearerToken = request.getHeader("Authorization");

        // 토큰이 있고 "Bearer "로 시작하면 토큰 값만 잘라서 리턴
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 토큰 없거나 형식이 맞지 않으면 null 리턴
        return null;
    }
}
