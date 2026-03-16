package com.runhub.rooms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomMemberDto {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String initials;
    private String role;
    private LocalDateTime joinedAt;
}
