package org.ktb.modie.v1.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChatDto {
    private Long chatId;
    private Long userId;
    private String message;
    private String sender;
    private LocalDateTime timestamp;

    @JsonCreator
    public ChatDto(
        @JsonProperty("chatId") Long chatId,
        @JsonProperty("userId") Long userId,
        @JsonProperty("message") String message,
        @JsonProperty("sender") String sender,
        @JsonProperty("timestamp") LocalDateTime timestamp) {
        this.chatId = chatId;
        this.userId = userId;
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }
}
