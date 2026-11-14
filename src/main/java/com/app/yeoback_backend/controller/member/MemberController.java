package com.app.yeoback_backend.controller.member;

import com.app.yeoback_backend.auth.JwtTokenProvider;
import com.app.yeoback_backend.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member/**")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletResponse response;

    // 회원가입 페이지 이동
    @GetMapping("join")
    public String goTOJoinPage() {
        // TODO -> 경로 상의 필요함
        return "member/join";
    }

    // 로그인 페이지 이동
    @GetMapping("login")
    public String login(@CookieValue(value = "remember", required = false) boolean remember,
                        @CookieValue(value = "rememberEmail", required = false) String rememberEmail,
                        Model model) {
        model.addAttribute("remember", remember);
        model.addAttribute("rememberEmail", rememberEmail);
        return "member/join";
    }



}
