package org.ktb.modie.presentation.v1.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatDto {
    private int chatId;
    private String userId;
    private String content;    // message를 content로 변경
    private String nickname;  // sender를 nickname으로 변경
    private String dateTime;  // LocalDateTime을 String으로 변경
    private String meetId;
    @JsonProperty("isOwner")
    private boolean isOwner;
    @JsonProperty("isMe")
    private boolean isMe;

    @JsonCreator
    public ChatDto(
        @JsonProperty("chatId") int chatId,
        @JsonProperty("userId") String userId,
        @JsonProperty("content") String content,
        @JsonProperty("nickname") String nickname,
        @JsonProperty("dateTime") String dateTime,
        @JsonProperty("meetId") String meetId,
        @JsonProperty("isOwner") boolean isOwner,
        @JsonProperty("isMe") boolean isMe) {
        this.chatId = chatId;
        this.userId = userId;
        this.content = content;
        this.nickname = nickname;

        // LocalDateTime을 문자열로 변환하고 밀리초 제거
        this.dateTime = dateTime;

        this.meetId = meetId;
        this.isOwner = isOwner;
        this.isMe = isMe;
    }
}
