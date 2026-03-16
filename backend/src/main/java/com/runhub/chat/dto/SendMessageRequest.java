package com.runhub.chat.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long communityId;
    private Long eventId;
    private String content;
}
