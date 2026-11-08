package com.app.yeoback_backend.auth.dto;

import com.app.yeoback_backend.member.MemberAuth;

/** 화면에 내려 주는 회원 정보 (비밀번호 · 상태값 제외) */
public record MemberResponse(
        Long id,
        String name,
        String alias,
        String email,
        String phone,
        String role) {

    public static MemberResponse from(MemberAuth m) {
        return new MemberResponse(
                m.getId(), m.getMemberName(), m.getMemberAlias(),
                m.getMemberEmail(), m.getMemberPhone(), m.roleOrDefault());
    }
}
