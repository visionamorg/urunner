package com.runhub.events.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventParticipantDto {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String status;
    private String role;
    private LocalDateTime registeredAt;
}
