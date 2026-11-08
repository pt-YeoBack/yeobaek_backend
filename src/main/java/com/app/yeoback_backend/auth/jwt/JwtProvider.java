package com.app.yeoback_backend.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/** JWT 액세스 토큰 발급 · 검증 */
@Component
public class JwtProvider {

    private static final String ROLE_CLAIM = "role";

    private final SecretKey key;
    private final long validitySeconds;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-seconds:3600}") long validitySeconds) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret 은 32바이트(영문 32자) 이상이어야 합니다.");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.validitySeconds = validitySeconds;
    }

    public String createAccessToken(Long memberId, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim(ROLE_CLAIM, role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validitySeconds * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 서명과 만료를 검사하고 회원 정보를 꺼낸다.
     * 위조 · 만료 · 형식 오류면 null.
     */
    public LoginMember parse(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new LoginMember(Long.valueOf(claims.getSubject()), claims.get(ROLE_CLAIM, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public long getValiditySeconds() {
        return validitySeconds;
    }
}
