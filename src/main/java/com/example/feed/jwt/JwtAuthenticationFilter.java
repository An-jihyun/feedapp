package com.example.feed.jwt;

import com.example.feed.repository.TokenBlacklistRepository;
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
    private final TokenBlacklistRepository blacklistRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 요청 헤더에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {

                if (blacklistRepository.existsByToken(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (SecurityException e) {
            throw new BadCredentialsException(JwtExceptionType.INVALID_SIGNATURE.name());
        } catch (MalformedJwtException e) {
            throw new BadCredentialsException(JwtExceptionType.MALFORMED_TOKEN.name());
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException(JwtExceptionType.EXPIRED_TOKEN.name());
        } catch (UnsupportedJwtException e) {
            throw new BadCredentialsException(JwtExceptionType.UNSUPPORTED_TOKEN.name());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(JwtExceptionType.EMPTY_TOKEN.name());
        }

        filterChain.doFilter(request, response);
    }
}