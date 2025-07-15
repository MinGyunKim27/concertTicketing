package org.example.concertTicketing.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secret;

    /**
     * 토큰 만들기
     */
    public String createJwt(Long userId, String userRole) {

        // 1. 서명 만들기
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        // 2. 데이터 준비
        Date now = new Date();                // 현재시간
        Date expiration = new Date(now.getTime() + 1000 * 1800); // 만료시간 설정 30분

        // 2. 토큰 만들기
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .claim("userRole", userRole)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰을 검증하고 memberId 를 반환합니다.
     */
    public long verifyToken(String token) {
        // 1. 서명 만들기
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        // 2. 검증
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 2. 사용자 추출
        String subject = claims.getSubject();
        // String value1  = (String) claims.get("key1"); // 커스텀하게 설정한 요소 추출

        // 3. 타입 변환

        // 4. 반환
        return Long.parseLong(subject);
    }

    public Claims extractClaims(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
