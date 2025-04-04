package org.ktb.modie.presentation.v1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.core.util.HashIdUtil;
import org.ktb.modie.domain.Chat;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.presentation.v1.dto.ChatDto;
import org.ktb.modie.repository.ChatRepository;
import org.ktb.modie.repository.MeetRepository;
import org.ktb.modie.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ChatHistoryController {

	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final MeetRepository meetRepository;  // MeetRepository 추가
	private final HashIdUtil hashIdUtil;

	// 생성자 수정: MeetRepository도 주입받도록 수정
	public ChatHistoryController(ChatRepository chatRepository, UserRepository userRepository,
		MeetRepository meetRepository, HashIdUtil hashIdUtil) {
		this.chatRepository = chatRepository;
		this.userRepository = userRepository;
		this.meetRepository = meetRepository;
		this.hashIdUtil = hashIdUtil;
	}

	@GetMapping("/api/v1/chat/{meetId}")
	public ResponseEntity<SuccessResponse<List<ChatDto>>> getChatHistory(
		@PathVariable("meetId") String meetHashId,
		@RequestParam(value = "lastChatId", required = false) Long lastChatId,
		@RequestAttribute("userId") String loggedInUserId,
		HttpServletRequest request) {

		Long meetId = hashIdUtil.decode(meetHashId);
		System.out.println("getChatHistory start !! ");
		// 유저 정보 보완 (userId로 DB에서 조회)
		meetRepository.findById(meetId)
			.orElseThrow(() -> new BusinessException(
				CustomErrorCode.MEETING_NOT_FOUND,
				"알 수 없는 모임입니다."
			));

		List<Chat> chatList;
		Pageable pageable = PageRequest.of(0, 25);

		if (lastChatId == null || lastChatId == 0) {
			chatList = chatRepository.findTop25ByMeetIdOrderByCreatedAtDesc(meetId, pageable);
		} else {
			chatList = chatRepository.findByMeetIdAndMessageIdLessThanOrderByCreatedAtDesc(meetId, lastChatId,
				pageable);
		}

		List<ChatDto> chatDtoList = chatList.stream()
			.map(chat -> {
				User user = chat.getUser();
				Meet meet = chat.getMeet();

				// 방장 여부 확인
				boolean isOwner = meet.getOwner().getUserId().equals(chat.getUser().getUserId());

				// 본인 여부 확인
				boolean isMe = chat.getUser().getUserId().equals(loggedInUserId);

				// 변경된 DTO 필드명에 맞춰 생성자 호출
				String userHashId = hashIdUtil.encode(Long.parseLong(user.getUserId()));
				return new ChatDto(
					chat.getMessageId(),                        // chatId
					userHashId,                                 // userId
					chat.getMessageContent(),                   // content (이전의 message)
					user.getUserName(),                         // nickname (이전의 sender)
					chat.getCreatedAt().toString().split("\\.")[0],                        // dateTime
					meetHashId,                                     // meetId
					isOwner,                                    // isOwner
					isMe                                        // isMe
				);
			})
			.collect(Collectors.toList());

		System.out.println("chatDtoList size : " + chatDtoList.size());

		return SuccessResponse.of(chatDtoList).asHttp(HttpStatus.OK);
	}
}
