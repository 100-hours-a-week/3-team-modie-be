package org.ktb.modie.service;

import org.ktb.modie.domain.Chat;
import org.ktb.modie.repository.ChatRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat saveMessage(Chat message) {
        return chatRepository.save(message);
    }
}
