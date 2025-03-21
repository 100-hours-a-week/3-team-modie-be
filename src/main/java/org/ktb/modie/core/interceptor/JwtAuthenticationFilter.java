package org.ktb.modie.core.interceptor;

import java.io.IOException;

import org.ktb.modie.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService; // 토큰 검증 메서드를 포함

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // jwtService에서 토큰의 유효성 검증 (예: 서명, 만료시간, 클레임 등)
                if (jwtService.isTokenValid(token)) {
                    // 토큰이 유효하면 필요한 사용자 정보 등을 SecurityContext에 설정할 수 있습니다.
                    // 예를 들어, UsernamePasswordAuthenticationToken을 생성하여 setAuthentication() 호출
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        // 인증이 필요하지 않은 요청이거나, 토큰 검증이 완료된 경우 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}
