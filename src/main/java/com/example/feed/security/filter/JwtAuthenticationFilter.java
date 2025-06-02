package com.example.feed.security.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.feed.security.TokenBlacklistRepository;
import com.example.feed.security.jwt.JwtExceptionType;
import com.example.feed.security.jwt.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
			if (token != null && jwtTokenProvider.validateToken(token)) {

				if (blacklistRepository.existsByToken(token)) {
					log.warn("블랙리스트 토큰 접근 시도: {}", requestURI);
					request.setAttribute("exception", JwtExceptionType.LOGGED_OUT_TOKEN.name());
				} else {
					Authentication authentication = jwtTokenProvider.getAuthentication(token);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} else if (token == null) {
				log.debug("인증 토큰 없음: {}", requestURI);
			}

		} catch (SecurityException e) {
			log.error("JWT 시큐리티 예외: {}", e.getMessage());
			request.setAttribute("exception", JwtExceptionType.INVALID_SIGNATURE.name());
		} catch (MalformedJwtException e) {
			log.error("JWT 형식 오류: {}", e.getMessage());
			request.setAttribute("exception", JwtExceptionType.MALFORMED_TOKEN.name());
		} catch (ExpiredJwtException e) {
			log.info("만료된 토큰 사용 시도: {}", requestURI);
			request.setAttribute("exception", JwtExceptionType.EXPIRED_TOKEN.name());
		} catch (UnsupportedJwtException e) {
			log.error("지원하지 않는 JWT 형식: {}", e.getMessage());
			request.setAttribute("exception", JwtExceptionType.UNSUPPORTED_TOKEN.name());
		} catch (IllegalArgumentException e) {
			log.error("빈 JWT 토큰: {}", e.getMessage());
			request.setAttribute("exception", JwtExceptionType.EMPTY_TOKEN.name());
		}

		filterChain.doFilter(request, response);
	}

}