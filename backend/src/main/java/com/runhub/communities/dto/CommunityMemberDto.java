package com.runhub.communities.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityMemberDto {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String role;
    private LocalDateTime joinedAt;
}
