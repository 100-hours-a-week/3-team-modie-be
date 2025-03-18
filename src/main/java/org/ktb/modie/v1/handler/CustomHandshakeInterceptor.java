package org.ktb.modie.v1.handler;

import java.net.URI;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Map<String, Object> attributes) throws Exception {
        // 쿼리 파라미터에서 userId 추출
        URI uri = request.getURI();
        String query = uri.getQuery();
        String userId = getQueryParameter(query, "userId");

        if (userId != null) {
            // 세션에 userId 저장
            attributes.put("userId", userId);
        }
        return true; // 핸드셰이크 계속 진행
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Exception ex) {
        // 핸드셰이크 이후 처리할 로직 (필요시)
    }

    // 쿼리 파라미터에서 특정 값을 추출하는 메서드
    private String getQueryParameter(String query, String paramName) {
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }
}
