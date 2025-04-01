package org.ktb.modie.core.interceptor;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class ApiThrottlingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_SECOND = 5;

    private final Map<String, Deque<Long>> requestHistory = new ConcurrentHashMap<>();

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        String userKey = resolveUserKey(request);

        long now = System.currentTimeMillis();
        requestHistory.putIfAbsent(userKey, new ArrayDeque<>());
        Deque<Long> timestamps = requestHistory.get(userKey);

        synchronized (timestamps) {
            // 1초 이전 요청은 제거
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > 1000) {
                timestamps.pollFirst();
            }

            // 요청 초과 시 차단
            if (timestamps.size() >= MAX_REQUESTS_PER_SECOND) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8"); // ✅ 여기 추가
                response.getWriter()
                    .write(
                        "{\"success\":false,\"data\":{\"code\":\"E429\",\"message\":\"요청이 너무 많습니다. 잠시 후 다시 시도해주세요.\"}}");
                return;
            }

            timestamps.addLast(now);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveUserKey(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String userId = jwtService.extractUserId(token);
                return "user:" + userId;
            } catch (Exception e) {
                // 토큰 이상하면 IP 기준 제한
                return "anonymous:" + request.getRemoteAddr();
            }
        }
        return "anonymous:" + request.getRemoteAddr();
    }
}
