package org.ktb.modie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SockJS와 함께 엔드포인트 설정
        registry.addEndpoint("/wss")  // 클라이언트가 연결할 엔드포인트
            // .setAllowedOrigins("http://localhost:5173", "ws://localhost:5173") // http와 ws 프로토콜을 모두 허용
            .setAllowedOriginPatterns("*");
        // .withSockJS(); // SockJS 지원
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // 구독 채널
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트에서 보낼 때 사용하는 prefix
    }
}

