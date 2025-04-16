package org.ktb.modie.core.interceptor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ktb.modie.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@Component
public class ApiThrottlingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_SECOND = 50;
    private static final long TIME_WINDOW_MS = 1000; // 1초

    private final LoadingCache<String, Deque<Long>> requestHistory = CacheBuilder.newBuilder()
        .maximumSize(10_000) // 최대 클라이언트 수 제한
        .expireAfterWrite(1, TimeUnit.HOURS) // 1시간 후 자동 제거
        .build(new CacheLoader<String, Deque<Long>>() {
            @Override
            public Deque<Long> load(String key) {
                return new LinkedList<>();
            }
        });

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {
        String userKey = resolveUserKey(request);
        long now = System.currentTimeMillis();

        try {
            Deque<Long> timestamps = requestHistory.get(userKey);

            synchronized (timestamps) {
                // 1초보다 오래된 요청 제거
                while (!timestamps.isEmpty() && now - timestamps.peekFirst() > TIME_WINDOW_MS) {
                    timestamps.pollFirst();
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

                // 정상 요청 → 현재 시간 추가
                timestamps.addLast(now);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // 캐시 접근 중 에러 → 그냥 통과시킴 (필요 시 로깅)
            filterChain.doFilter(request, response);
        }
    }

    private String resolveUserKey(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String userId = jwtService.extractUserId(token);
                return "user:" + userId;
            } catch (Exception e) {
                // 토큰 파싱 실패 시 IP 기준 제한
                return "anonymous:" + request.getRemoteAddr();
            }
        }
        return "anonymous:" + request.getRemoteAddr();
    }
}
