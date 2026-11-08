package com.app.yeoback_backend.auth.dto;

/**
 * 로그인 요청 본문. 프론트 login(method, contact, password)와 같은 모양.
 *
 * @param method   "email" 또는 "phone"
 * @param contact  이메일 주소 또는 휴대폰 번호 (하이픈 있어도 됨)
 * @param password 비밀번호 (평문 — HTTPS로 보내야 한다)
 */
public record LoginRequest(String method, String contact, String password) {
}
