package org.ktb.modie.presentation.v1.dto;

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
    private Long meetId;
    private boolean isOwner;  // 방장 여부
    private boolean isSelf;   // 본인 작성 여부

    @JsonCreator
    public ChatDto(
        @JsonProperty("chatId") Long chatId,
        @JsonProperty("userId") Long userId,
        @JsonProperty("message") String message,
        @JsonProperty("sender") String sender,
        @JsonProperty("timestamp") LocalDateTime timestamp,
        @JsonProperty("meetId") Long meetId,
        @JsonProperty("isOwner") boolean isOwner,
        @JsonProperty("isSelf") boolean isSelf) {
        this.chatId = chatId;
        this.userId = userId;
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
        this.meetId = meetId;
        this.isOwner = isOwner;
        this.isSelf = isSelf;
    }
}
