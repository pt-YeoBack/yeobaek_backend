package com.app.yeoback_backend.controller.member;

import com.app.yeoback_backend.aop.annotation.LogStatus;
import com.app.yeoback_backend.auth.JwtTokenProvider;
import com.app.yeoback_backend.dto.MemberDTO;
import com.app.yeoback_backend.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/member/**")
@RequiredArgsConstructor
@Slf4j
public class MemberAPIController {
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletResponse response;

    // 회원가입
    @PostMapping("join")
    @LogStatus
    public ResponseEntity<?> join(MemberDTO memberDTO) {
        log.info("memberDTO {}", memberDTO);
        memberService.join(memberDTO);

        return ResponseEntity.ok(Map.of("message", "회원가입 성공"));
    }
}
