package com.example.feed.jwt;

import lombok.Getter;

@Getter
public enum JwtExceptionType {

    LOGGED_OUT_TOKEN("로그아웃된 토큰입니다.", 401),
    INVALID_SIGNATURE("잘못된 JWT 서명입니다.", 401),
    MALFORMED_TOKEN("JWT 토큰 형식이 잘못되었습니다.", 400),
    EXPIRED_TOKEN("토큰이 만료되었습니다.", 401),
    UNSUPPORTED_TOKEN("지원하지 않는 JWT 토큰입니다.", 401),
    EMPTY_TOKEN("JWT claims 문자열이 비어 있습니다.", 400),
    AUTHENTICATION_REQUIRED("인증이 필요합니다.", 401);

    private final String message;
    private final int status;

    JwtExceptionType(String message, int status) {
        this.message = message;
        this.status = status;
    }
}