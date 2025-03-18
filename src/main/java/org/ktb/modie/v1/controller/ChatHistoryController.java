package org.ktb.modie.v1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.ktb.modie.domain.Chat;
import org.ktb.modie.domain.User;
import org.ktb.modie.repository.ChatRepository;
import org.ktb.modie.repository.UserRepository;
import org.ktb.modie.v1.dto.ChatDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatHistoryController {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatHistoryController(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/chat/{meetId}")
    public List<ChatDto> getChatHistory(@PathVariable Long meetId, @RequestParam(required = false) Long lastChatId) {

        List<Chat> chatList;
        if (lastChatId == null || lastChatId == 0) {
            chatList = chatRepository.findTop25ByMeetIdOrderByCreatedAtDesc(meetId);  // 최신 25개 채팅 내역 불러오기
        } else {
            Pageable pageable = PageRequest.of(0, 25);  // 25개씩 불러옴
            chatList = chatRepository.findByMeetIdAndMessageIdLessThanOrderByCreatedAtDesc(meetId, lastChatId,
                pageable);
        }

        List<ChatDto> chatDTOList = chatList.stream()
            .map(chat -> {
                // userId로 User 조회
                User user = userRepository.findByUserId(chat.getUserId());

                // 만약 user가 null이라면 예외를 던짐
                if (user == null) {
                    throw new RuntimeException("User not found for userId: " + chat.getUserId());
                }

                // messageId를 Long으로 변환
                Long messageId = Long.valueOf(chat.getMessageId());  // Integer에서 Long으로 변환

                return new ChatDto(
                    messageId, // Long 타입으로 변환된 messageId
                    chat.getMeetId(),
                    chat.getMessageContent(),
                    user.getUserName(),
                    chat.getCreatedAt());
            })
            .collect(Collectors.toList());

        return chatDTOList;
    }
}
