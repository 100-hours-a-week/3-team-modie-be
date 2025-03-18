package org.ktb.modie.v1.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ktb.modie.repository.ChatRepository;
import org.ktb.modie.repository.MeetRepository;
import org.ktb.modie.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final UserRepository userRepository;
    private final MeetRepository meetRepository;
    private final ChatRepository chatRepository;
    // meetId에 대한 WebSocket 세션 관리
    private Map<String, Map<String, WebSocketSession>> meetSessions = new HashMap<>();

    // 📌 ChatRepository를 생성자 주입
    public ChatWebSocketHandler(ChatRepository chatRepository, UserRepository userRepository,
        MeetRepository meetRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.meetRepository = meetRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String meetId = getMeetIdFromSession(session);  // URL에서 meetId 추출
        meetSessions.putIfAbsent(meetId, new HashMap<>());
        meetSessions.get(meetId).put(session.getId(), session);  // 해당 meetId에 세션 저장
        System.out.println("Connection established for meetId " + meetId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String meetId = getMeetIdFromSession(session);  // URL에서 meetId 추출
        String userId = "1";
        System.out.println("Received message: " + message.getPayload() + " from meetId: " + meetId);
        //
        // // 1. 메시지를 DB에 저장
        // Chat chat = new Chat();
        // chat.setMeet(new Meet(Integer.parseInt(meetId)));  // meetId 설정
        // chat.setUser(new User(Integer.parseInt(userId)));  // userId 설정
        // chat.setMessageContent(message.getPayload());
        // chat.setCreatedAt(LocalDateTime.now());
        //
        // chatRepository.save(chat);  // DB에 저장

        // 메시지 브로드캐스트: 해당 meetId에 연결된 모든 세션에게 전송
        Map<String, WebSocketSession> sessions = meetSessions.get(meetId);
        if (sessions != null) {
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(message);  // 세션에 메시지 전송
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String meetId = getMeetIdFromSession(session);
        if (meetSessions.containsKey(meetId)) {
            meetSessions.get(meetId).remove(session.getId());  // 세션 종료 시 해당 meetId에서 세션 제거
            if (meetSessions.get(meetId).isEmpty()) {
                meetSessions.remove(meetId);  // 모든 클라이언트가 나갔으면 해당 meetId 제거
            }
        }
        System.out.println("Connection closed for meetId " + meetId);
    }

    // 세션에서 meetId를 추출하는 방법 수정
    private String getMeetIdFromSession(WebSocketSession session) {
        String uri = session.getUri().toString();  // WebSocket URL 가져오기
        // URL에서 "/meets/{meetId}/chat" 부분을 찾아 meetId 추출
        String[] parts = uri.split("/");
        String meetId = parts[parts.length - 2];  // /meets/{meetId}/chat에서 {meetId} 추출
        return meetId;
    }

}
