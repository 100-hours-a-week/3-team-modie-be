package org.ktb.modie.v1.controller;

import java.time.LocalDateTime;

import org.ktb.modie.domain.Chat;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{meetId}") // 클라이언트에서 "/app/chat/{meetId}"로 메시지 전송
    @SendTo("/topic/chat/{meetId}") // 구독 중인 모든 클라이언트에게 메시지 전달
    public Chat sendMessage(@DestinationVariable String meetId, Chat message) {
        message.setCreatedAt(LocalDateTime.now()); // 메시지 생성 시간 설정

        // 필드 값이 채워졌는지 확인
        System.out.println("Received message: " + message.getMessageContent());
        System.out.println("User: " + message.getUser());
        System.out.println("Meet: " + message.getMeet());
        return message; // 메시지를 그대로 반환하면 구독자들에게 전달됨
    }
}
