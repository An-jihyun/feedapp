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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistRepository blacklistRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);
        String requestURI = request.getRequestURI();

        try {
            if (token == null) {
                log.debug("인증 토큰 없음: {}", requestURI);
                // 예외 던지지 않고 다음 필터로 넘어감 → 익명 사용자로 간주
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtTokenProvider.validateToken(token)) {

                if (blacklistRepository.existsByToken(token)) {
                    log.warn("블랙리스트 토큰 접근 시도: {}", requestURI);
                    throw new BadCredentialsException(JwtExceptionType.LOGGED_OUT_TOKEN.name());
                }

                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (SecurityException e) {
            log.error("JWT 시큐리티 예외: {}", e.getMessage());
            throw new BadCredentialsException(JwtExceptionType.INVALID_SIGNATURE.name());
        } catch (MalformedJwtException e) {
            log.error("JWT 형식 오류: {}", e.getMessage());
            throw new BadCredentialsException(JwtExceptionType.MALFORMED_TOKEN.name());
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰 사용 시도: {}", requestURI);
            throw new CredentialsExpiredException(JwtExceptionType.EXPIRED_TOKEN.name());
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 형식: {}", e.getMessage());
            throw new BadCredentialsException(JwtExceptionType.UNSUPPORTED_TOKEN.name());
        } catch (IllegalArgumentException e) {
            log.error("빈 JWT 토큰: {}", e.getMessage());
            throw new BadCredentialsException(JwtExceptionType.EMPTY_TOKEN.name());
        }

        filterChain.doFilter(request, response);
    }
}