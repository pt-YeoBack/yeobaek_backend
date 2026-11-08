package com.app.yeoback_backend.member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 로그인·인증에 필요한 회원 정보 (tbl_member + tbl_auth 조인 결과).
 * 비밀번호 해시를 담고 있으므로 응답으로 그대로 내보내지 않는다.
 */
@Getter
@Setter
@ToString(exclude = "memberPassword")
public class MemberAuth {
    private Long id;
    private String memberEmail;
    private String memberPhone;
    private String memberPassword;
    private String memberName;
    private String memberAlias;
    private String memberStatus; // ACTIVE · SUSPENDED · WITHDRAWN
    private String memberRole;   // ADMIN · MEMBER (tbl_auth가 없으면 null)
    private String memberType;   // YEOBEAK · NAVER · KAKAO · GOOGLE

    public boolean isActive() {
        return "ACTIVE".equals(memberStatus);
    }

    public String roleOrDefault() {
        return memberRole == null ? "MEMBER" : memberRole;
    }
}
