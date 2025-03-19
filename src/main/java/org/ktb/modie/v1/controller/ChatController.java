package org.ktb.modie.v1.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.Chat;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.repository.ChatRepository;
import org.ktb.modie.repository.MeetRepository;
import org.ktb.modie.repository.UserRepository;
import org.ktb.modie.service.ChatService;
import org.ktb.modie.v1.dto.ChatMessageDto;
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

	@MessageMapping("/chat/{meetId}")
	public void sendMessage(@DestinationVariable Long meetId, ChatMessageDto message,
		SimpMessageHeaderAccessor headerAccessor) {

		// 프론트엔드에서 보낸 createdAt 값이 Date 객체일 경우, 이를 LocalDateTime으로 변환
		LocalDateTime createdAt = LocalDateTime.parse(message.getCreatedAt().toString());
		message.setCreatedAt(createdAt);

		// 현재 사용자의 세션 ID 가져오기
		String senderSessionId = headerAccessor.getSessionId();

		// 유저 정보 보완 (userId로 DB에서 조회)
		User user = userRepository.findById(message.getUserId())
			.orElseThrow(() -> new BusinessException(
				CustomErrorCode.INVALID_PARAMETER_VALUE,
				String.format("%s 알 수 없는 사용자입니다.", message.getUserId())
			));

		Meet meet = meetRepository.findById(meetId)
			.orElseThrow(() -> new BusinessException(
				CustomErrorCode.INVALID_PARAMETER_VALUE, "존재하지 않는 모임 ID입니다."
			));

		// Map에 필요한 데이터만 담기 (userId, userName, messageContent, createdAt)
		Map<String, Object> payload = new HashMap<>();
		payload.put("userId", message.getUserId());  // userId
		payload.put("userName", user.getUserName()); // userName
		payload.put("messageContent", message.getMessageContent()); // messageContent
		payload.put("createdAt", message.getCreatedAt()); // createdAt

		Chat chat = Chat.builder()
			.user(user)
			.messageContent(message.getMessageContent())
			.createdAt(LocalDateTime.now())
			.meet(meet)
			.build();

		// Chat 객체 저장
		chatRepository.save(chat);

		// 모든 구독자에게 전송하되, 보낸 사람의 세션 ID를 가진 사람에게는 전송하지 않음
		messagingTemplate.convertAndSend("/topic/chat/" + meetId, payload, msg -> {
			String destinationSessionId = msg.getHeaders()
				.get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
			if (!Objects.equals(senderSessionId, destinationSessionId)) {
				return msg;
			}
			return null;
		});
	}

}
