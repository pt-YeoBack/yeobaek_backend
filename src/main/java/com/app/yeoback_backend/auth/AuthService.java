package com.app.yeoback_backend.auth;

import com.app.yeoback_backend.auth.dto.LoginRequest;
import com.app.yeoback_backend.auth.dto.LoginResponse;
import com.app.yeoback_backend.auth.dto.MemberResponse;
import com.app.yeoback_backend.auth.jwt.JwtProvider;
import com.app.yeoback_backend.common.ApiException;
import com.app.yeoback_backend.member.MemberAuth;
import com.app.yeoback_backend.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    /** 계정이 없는지 · 비밀번호가 틀렸는지 알 수 없도록 같은 문구를 쓴다 (프론트 testAuthService와 동일 정책) */
    private static final String LOGIN_FAILED = "아이디 또는 비밀번호가 올바르지 않아요.";

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest req) {
        if (req == null || isBlank(req.method()) || isBlank(req.contact()) || isBlank(req.password())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "로그인 정보를 모두 입력해 주세요.");
        }

        MemberAuth member = switch (req.method()) {
            case "email" -> memberMapper.findByEmail(req.contact().trim().toLowerCase(Locale.ROOT));
            case "phone" -> memberMapper.findByPhone(req.contact().replaceAll("\\D", ""));
            default -> throw new ApiException(HttpStatus.BAD_REQUEST, "method 는 email 또는 phone 이어야 해요.");
        };

        if (member == null || member.getMemberPassword() == null
                || !passwordEncoder.matches(req.password(), member.getMemberPassword())) {
            // 401이 아니라 400: 프론트 ajax.jsx 는 401을 "세션 만료 → 자동 로그아웃"으로 처리하므로
            // 401은 토큰 문제(없음·만료·위조)에만 쓴다
            throw new ApiException(HttpStatus.BAD_REQUEST, LOGIN_FAILED);
        }
        if (!member.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "사용할 수 없는 계정이에요. (정지 또는 탈퇴)");
        }

        String token = jwtProvider.createAccessToken(member.getId(), member.roleOrDefault());
        return new LoginResponse(token, "Bearer", jwtProvider.getValiditySeconds(), MemberResponse.from(member));
    }

    public MemberResponse me(Long memberId) {
        MemberAuth member = memberMapper.findById(memberId);
        if (member == null || !member.isActive()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "다시 로그인해 주세요.");
        }
        return MemberResponse.from(member);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
