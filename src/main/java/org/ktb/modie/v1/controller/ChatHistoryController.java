package org.ktb.modie.v1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.ktb.modie.domain.Chat;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.repository.ChatRepository;
import org.ktb.modie.repository.MeetRepository;
import org.ktb.modie.repository.UserRepository;
import org.ktb.modie.v1.dto.ChatDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ChatHistoryController {

	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final MeetRepository meetRepository;  // MeetRepository 추가

	// 생성자 수정: MeetRepository도 주입받도록 수정
	public ChatHistoryController(ChatRepository chatRepository, UserRepository userRepository,
		MeetRepository meetRepository) {
		this.chatRepository = chatRepository;
		this.userRepository = userRepository;
		this.meetRepository = meetRepository;
	}

	@GetMapping("/api/chat/{meetId}")
	public List<ChatDto> getChatHistory(@PathVariable Long meetId, @RequestParam(required = false) Long lastChatId,
		HttpServletRequest request) {

		// 로그인한 사용자 ID를 쿠키에서 가져오기 (로그인 정보가 쿠키에 저장되어 있다고 가정)
		String loggedInUserId = getLoggedInUserIdFromCookies(request);
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
				boolean isSelf = chat.getUser().getUserId().equals(loggedInUserId);

				return new ChatDto(
					chat.getMessageId().longValue(),
					Long.parseLong(chat.getUser().getUserId()),
					chat.getMessageContent(),
					user.getUserName(),
					chat.getCreatedAt(),
					meetId,
					isOwner,
					isSelf
				);
			})
			.collect(Collectors.toList());

		return chatDtoList;
	}

	// 쿠키에서 로그인한 사용자 ID를 가져오는 메소드 (예시)
	private String getLoggedInUserIdFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String loggedInUserId = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("userId".equals(cookie.getName())) {
					loggedInUserId = cookie.getValue();
					break;
				}
			}
		}

		return loggedInUserId; // 쿠키에서 가져온 사용자 ID 반환
	}

}
