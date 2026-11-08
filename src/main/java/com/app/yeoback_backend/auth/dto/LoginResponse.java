package com.app.yeoback_backend.auth.dto;

/**
 * 로그인 성공 응답.
 * 프론트는 accessToken 을 localStorage("token")에 저장하면 ajax.jsx 가 이후 요청에 Bearer 로 붙인다.
 */
public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        MemberResponse member) {
}
