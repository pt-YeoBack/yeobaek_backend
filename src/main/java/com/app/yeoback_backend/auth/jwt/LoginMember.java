package com.app.yeoback_backend.auth.jwt;

/**
 * 토큰에서 꺼낸 "지금 요청한 사람" 정보.
 * 컨트롤러에서 @AuthenticationPrincipal LoginMember loginMember 로 받는다.
 */
public record LoginMember(Long id, String role) {
}
