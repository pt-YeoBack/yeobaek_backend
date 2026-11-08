package com.app.yeoback_backend.auth;

import com.app.yeoback_backend.auth.dto.LoginRequest;
import com.app.yeoback_backend.auth.dto.LoginResponse;
import com.app.yeoback_backend.auth.dto.MemberResponse;
import com.app.yeoback_backend.auth.jwt.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ajax/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 로그인 — 누구나 호출 가능 (SecurityConfig에서 permitAll) */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** 내 정보 — 토큰이 있어야 호출 가능. 프론트에서 새로고침 후 로그인 상태 복원용 */
    @GetMapping("/me")
    public MemberResponse me(@AuthenticationPrincipal LoginMember loginMember) {
        return authService.me(loginMember.id());
    }
}
