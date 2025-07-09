package org.example.concertTicketing.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.concertTicketing.domain.user.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

//    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain chain) throws ServletException, IOException {
        // 1. Authorization 헤더 추출
        String bearerJwt = request.getHeader("Authorization");
        String uri = request.getRequestURI();

        // 2. 인증 필요 없는 URI는 필터 통과
        if (uri.startsWith("/api/auth")) {
            chain.doFilter(request, response); // 필터 무시하고 통과
            return;
        }

        // 3. 토큰 없는 경우 400 반환
        if (bearerJwt == null || !bearerJwt.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 필요합니다.");
            return;
        }

        try {
            // Bearer 제거하고 토큰만 추출
            String jwt = bearerJwt.substring(7);

            // JWT 유효성 검사와 claims 추출
            Claims claims = jwtService.extractClaims(jwt);
            if (claims == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            // 사용자 정보 추출
            Long userId = Long.parseLong(claims.getSubject());
            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + userRole))
            );

            SecurityContextHolder.getContext().setAuthentication(token);
            chain.doFilter(request, response);

        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("Internal server error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
