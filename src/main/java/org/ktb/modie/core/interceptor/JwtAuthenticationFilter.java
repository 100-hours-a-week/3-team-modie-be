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
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            request.setAttribute("userId", "");
        }

        if (authHeader != null && authHeader.startsWith("Bearer")) {
            String token = authHeader.substring(6).trim();
            try {
                // jwtService에서 토큰의 유효성 검증
                if (jwtService.isTokenValid(token)) {
                    // 토큰에서 userId(sub) 추출
                    String userId = jwtService.extractUserId(token);

                    // 요청 속성에 userId 추가
                    request.setAttribute("userId", userId);
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}
