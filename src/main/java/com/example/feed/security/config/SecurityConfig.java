package com.example.feed.security.config;

import com.example.feed.security.exception.JwtAccessDeniedHandler;
import com.example.feed.security.exception.JwtAuthenticationEntryPoint;
import com.example.feed.security.filter.JwtAuthenticationFilter;
import com.example.feed.security.jwt.JwtTokenProvider;
import com.example.feed.auth.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity //  @PreAuthorize 사용을 위해
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/signup").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패 처리
                        .accessDeniedHandler(jwtAccessDeniedHandler)           // 권한 부족 처리
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, tokenBlacklistRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
