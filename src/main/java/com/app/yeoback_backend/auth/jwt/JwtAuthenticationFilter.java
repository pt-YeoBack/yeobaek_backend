package com.app.yeoback_backend.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 모든 요청에서 "Authorization: Bearer 토큰"을 읽어 로그인 상태로 만든다.
 * 토큰이 없거나 잘못됐으면 그냥 통과시키고, 막을지는 SecurityConfig 규칙이 정한다.
 * (@Component 로 등록하지 않는다 — 서블릿 필터로 한 번 더 등록되는 것을 막기 위해 SecurityConfig에서 직접 생성)
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(PREFIX)) {
            LoginMember member = jwtProvider.parse(header.substring(PREFIX.length()));
            if (member != null) {
                var auth = new UsernamePasswordAuthenticationToken(
                        member, null, List.of(new SimpleGrantedAuthority("ROLE_" + member.role())));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }
}
