package org.ktb.modie.v1.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private String messageContent;
    private String userId;
    private String userName;
    private LocalDateTime createdAt;
    private Long meetId;

    // getters and setters
}

