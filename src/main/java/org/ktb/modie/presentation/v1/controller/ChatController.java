package org.ktb.modie.presentation.v1.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.core.util.HashIdUtil;
import org.ktb.modie.domain.Chat;
import org.ktb.modie.domain.FcmToken;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.domain.UserMeet;
import org.ktb.modie.presentation.v1.dto.ChatDto;
import org.ktb.modie.repository.ChatRepository;
import org.ktb.modie.repository.FcmTokenRepository;
import org.ktb.modie.repository.MeetRepository;
import org.ktb.modie.repository.UserMeetRepository;
import org.ktb.modie.repository.UserRepository;
import org.ktb.modie.service.ChatService;
import org.ktb.modie.service.FcmService;
import org.ktb.modie.service.JwtService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final MeetRepository meetRepository;
    private final JwtService jwtService;
    private final FcmService fcmService;
    private final UserMeetRepository userMeetRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final HashIdUtil hashIdUtils;

    @MessageMapping("/chat/{meetId}")
    public void sendMessage(
        @DestinationVariable("meetId") Long meetId,
        String messageContent,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        // JWT 토큰에서 userId 추출
        List<String> authHeaders = headerAccessor.getNativeHeader("Authorization");
        String userId = extractUserIdFromToken(authHeaders);

        // 유저 정보 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                CustomErrorCode.INVALID_PARAMETER_VALUE,
                String.format("%s 알 수 없는 사용자입니다.", "userId")
            ));

        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new BusinessException(
                CustomErrorCode.INVALID_PARAMETER_VALUE, "존재하지 않는 모임 ID입니다."
            ));

        LocalDateTime now = LocalDateTime.now();

        // 방장 여부 확인
        boolean isOwner = meet.getOwner().getUserId().equals(userId);

        // 메시지 ID 생성 (중복 방지용)
        String messageId = UUID.randomUUID().toString();

        // DB에 저장
        Chat chat = Chat.builder()
            .user(user)
            .messageContent(messageContent)
            .createdAt(now)
            .meet(meet)
            .build();
        chatRepository.save(chat);

        // 기존 ChatDto 객체 생성
        ChatDto chatDto = ChatDto.builder()
            .chatId(chat.getMessageId())
            .userId(userId)
            .nickname(user.getUserName())
            .content(messageContent)
            .dateTime(now.toString().split("\\.")[0])
            .meetId(meetId)
            .isOwner(isOwner)
            .isMe(false)  // 다른 사용자용 기본값
            .build();

        // 발신자용 메시지 (isMe = true)
        ChatDto senderChatDto = ChatDto.builder()
            .chatId(chat.getMessageId())
            .userId(userId)
            .nickname(user.getUserName())
            .content(messageContent)
            .dateTime(now.toString().split("\\.")[0])
            .meetId(meetId)
            .isOwner(isOwner)
            .isMe(true)  // 발신자용은 true로 설정
            .build();

        // 모든 사용자에게 isMe = false로 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + meetId, chatDto);

        // 발신자에게만 isMe = true로 메시지 전송
        messagingTemplate.convertAndSend("/user/" + userId + "/chat/" + meetId, senderChatDto);

        System.out.println("메시지 전송 완료 - 일반: /topic/chat/" + meetId
            + ", 발신자: /user/" + userId + "/chat/" + meetId);

        // FCM 알림 전송
        // 모임 참여자 조회 (+본인 제외)
        List<UserMeet> userMeets = userMeetRepository.findUserMeetByMeet_MeetIdAndDeletedAtIsNull(meetId);
        List<String> targetUserIds = userMeets.stream()
            .map(userMeet -> userMeet.getUser().getUserId())
            .filter(id -> !id.equals(userId)) // 본인 제외
            .toList();
        // 해당 참여자들의 FCM 토큰 한 번에 조회
        List<FcmToken> fcmTokens = fcmTokenRepository.findByUser_UserIdIn(targetUserIds);

        for (FcmToken fcmToken : fcmTokens) {
            if (fcmToken.getToken() == null || fcmToken.getToken().isBlank()) {
                throw new BusinessException(CustomErrorCode.FCM_TOKEN_NOT_FOUND);
            }

            try {
                String title = user.getUserName() + "님의 새 메시지";
                String body = messageContent;
                fcmService.sendNotification(fcmToken.getToken(), title, body, meetId);
            } catch (Exception e) {
                throw new BusinessException(CustomErrorCode.FCM_SEND_FAILED);
            }
        }
    }

    private String extractUserIdFromToken(List<String> authHeaders) {
        if (authHeaders == null || authHeaders.isEmpty()) {
            throw new BusinessException(
                CustomErrorCode.INVALID_PARAMETER_VALUE,
                "인증 헤더가 필요합니다."
            );
        }

        String authHeader = authHeaders.get(0);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(
                CustomErrorCode.INVALID_PARAMETER_VALUE,
                "유효하지 않은 인증 헤더입니다."
            );
        }

        String token = authHeader.substring(7);
        try {
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            throw new BusinessException(
                CustomErrorCode.INVALID_PARAMETER_VALUE,
                "유효하지 않은 토큰입니다."
            );
        }
    }
}
