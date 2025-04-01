package org.ktb.modie.presentation.v1.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.Chat;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.presentation.v1.dto.ChatDto;
import org.ktb.modie.repository.ChatRepository;
import org.ktb.modie.repository.MeetRepository;
import org.ktb.modie.repository.UserRepository;
import org.ktb.modie.service.ChatService;
import org.ktb.modie.service.JwtService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final MeetRepository meetRepository;
    private final JwtService jwtService;

    @MessageMapping("/chat/{meetId}")
    public void sendMessage(
        @DestinationVariable Long meetId,
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

        if (messageContent == null || messageContent.trim().isEmpty()) {
            throw new BusinessException(
                CustomErrorCode.INVALID_PARAMETER_VALUE,
                "메시지 내용이 비어있습니다."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        // completedAt 기준으로 현재 날짜보다 이전이면 예외 처리
        if (meet.getCompletedAt() != null && meet.getCompletedAt().isBefore(now)) {
            throw new BusinessException(
                CustomErrorCode.INVALID_PARAMETER_VALUE,
                "이미 종료된 모임에서는 메시지를 전송할 수 없습니다."
            );
        }

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

        try {
            chatRepository.save(chat);
        } catch (DataIntegrityViolationException ex) {
            // 예외 메시지 로그 (추후 오류 추적을 위해)
            log.error("DB 저장 오류: {}", ex.getMessage(), ex);

            throw new BusinessException(
                CustomErrorCode.INTERNAL_SERVER_ERROR,
                "메시지 전송 중 오류가 발생했습니다. 다시 시도해주세요."
            );
        } catch (Exception ex) {
            // 예외 메시지 로그 (기타 예외 처리)
            log.error("알 수 없는 오류가 발생했습니다: {}", ex.getMessage(), ex);

            throw new BusinessException(
                CustomErrorCode.INTERNAL_SERVER_ERROR,
                "메시지 전송 중 예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            );
        }

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

        try {
            messagingTemplate.convertAndSend("/topic/chat/" + meetId, chatDto);
            messagingTemplate.convertAndSend("/user/" + userId + "/chat/" + meetId, senderChatDto);
        } catch (MessagingException ex) {
            // 예외 메시지 로그 (추후 오류 추적을 위해)
            log.error("메시지 전송 오류: {}", ex.getMessage(), ex);

            throw new BusinessException(
                CustomErrorCode.INTERNAL_SERVER_ERROR,
                "메시지 전송 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            );
        } catch (Exception ex) {
            // 예외 메시지 로그 (기타 예외 처리)
            log.error("알 수 없는 오류가 발생했습니다: {}", ex.getMessage(), ex);

            throw new BusinessException(
                CustomErrorCode.INTERNAL_SERVER_ERROR,
                "메시지 전송 중 예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            );
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
