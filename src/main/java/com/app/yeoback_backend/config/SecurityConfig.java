package com.app.yeoback_backend.config;

import com.app.yeoback_backend.auth.jwt.JwtAuthenticationFilter;
import com.app.yeoback_backend.auth.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 보안 규칙
 * - 세션을 쓰지 않고 JWT(Authorization: Bearer)로만 로그인 상태를 판단한다.
 * - /ajax/auth/login 은 누구나, 나머지 /ajax/** 는 로그인한 사람만.
 */
@Configuration
public class SecurityConfig {

    /** 로그인 없이 호출할 수 있는 주소 */
    private static final String[] PUBLIC_URLS = {
            "/ajax/auth/login",
            "/error",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtProvider jwtProvider) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)            // 쿠키 세션이 아니라 토큰 방식이라 CSRF 토큰 불필요
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)       // 스프링 기본 로그인 페이지 끄기
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)          // 로그아웃은 프론트에서 토큰을 지우면 끝
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        // 토큰 없음 · 만료 → 401 (프론트 ajax.jsx 가 401을 받으면 로그인 화면으로 보낸다)
                        .authenticationEntryPoint((req, res, ex) ->
                                writeJson(res, HttpStatus.UNAUTHORIZED, "로그인이 필요해요."))
                        // 로그인은 했지만 권한 부족 → 403
                        .accessDeniedHandler((req, res, ex) ->
                                writeJson(res, HttpStatus.FORBIDDEN, "접근 권한이 없어요.")))
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** 비밀번호는 BCrypt 해시로 저장 · 비교한다 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Vite 개발 서버(5173)에서 프록시 없이 직접 호출할 때를 위한 허용 목록 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/ajax/**", config);
        return source;
    }

    private static void writeJson(jakarta.servlet.http.HttpServletResponse res, HttpStatus status, String message)
            throws java.io.IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
