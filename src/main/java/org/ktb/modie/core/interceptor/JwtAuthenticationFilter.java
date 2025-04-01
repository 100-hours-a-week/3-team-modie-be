package org.ktb.modie.core.interceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Qualifier("objectMapper")
    @Autowired
    private ObjectMapper objectMapper;

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
                if (jwtService.isTokenValid(token)) {
                    String userId = jwtService.extractUserId(token);
                    request.setAttribute("userId", userId);
                } else {
                    throw new BusinessException(CustomErrorCode.INVALID_TOKEN);
                }
            } catch (BusinessException e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                // JSON 형태로 응답 객체 구성
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);

                Map<String, String> data = new HashMap<>();
                data.put("code", e.getErrorCode().getCode());
                data.put("message", e.getErrorCode().getMessage());

                errorResponse.put("data", data);

                // JSON 문자열로 변환 후 응답
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                return;
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
