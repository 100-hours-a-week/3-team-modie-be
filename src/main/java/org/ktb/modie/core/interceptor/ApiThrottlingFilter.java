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
    private static final long TIME_WINDOW_MS = 1000; // 1초

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
            // 1초 초과된 오래된 요청 제거
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > TIME_WINDOW_MS) {
                timestamps.pollFirst();
            }

            // timestamps가 비어 있으면 userKey 자체 제거
            if (timestamps.isEmpty()) {
                requestHistory.remove(userKey);
            }

            // 요청 초과 시 차단
            if (timestamps.size() >= MAX_REQUESTS_PER_SECOND) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                    "{\"success\":false,\"data\":{\"code\":\"E429\","
                        + "\"message\":\"요청이 너무 많습니다. 잠시 후 다시 시도해주세요.\"}}");
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
                // 토큰 파싱 실패 시 IP 기준으로 제한
                return "anonymous:" + request.getRemoteAddr();
            }
        }
        return "anonymous:" + request.getRemoteAddr();
    }
}
