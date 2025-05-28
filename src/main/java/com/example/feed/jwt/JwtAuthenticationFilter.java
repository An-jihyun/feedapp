package com.example.feed.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT 토큰 처리를 위한 JwtTokenProvider를 의존성 주입
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (SecurityException e) {
            throw new BadCredentialsException("INVALID_SIGNATURE", e);
        } catch (MalformedJwtException e) {
            throw new BadCredentialsException("MALFORMED_TOKEN", e);
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("EXPIRED_TOKEN", e);
        } catch (UnsupportedJwtException e) {
            throw new BadCredentialsException("UNSUPPORTED_TOKEN", e);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("EMPTY_TOKEN", e);
        }

        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더 'Authorization' 에서 Bearer 토큰 추출 메서드
    private String resolveToken(HttpServletRequest request) {

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