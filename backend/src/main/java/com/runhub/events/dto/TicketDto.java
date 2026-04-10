package com.runhub.events.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketDto {
    private Long eventId;
    private String eventName;
    private Long userId;
    private String username;
    private String status;
    private String qrToken;
    private Boolean checkedIn;
    private LocalDateTime checkedInAt;
    private LocalDateTime registeredAt;
}
