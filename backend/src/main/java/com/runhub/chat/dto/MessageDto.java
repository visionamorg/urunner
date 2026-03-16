package com.runhub.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private Long communityId;
    private Long eventId;
    private String content;
    private LocalDateTime sentAt;
}
