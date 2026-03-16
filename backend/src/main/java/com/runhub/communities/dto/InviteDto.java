package com.runhub.communities.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InviteDto {
    private Long id;
    private Long communityId;
    private String communityName;
    private String communityImageUrl;
    private Long invitedUserId;
    private String invitedUsername;
    private String invitedByUsername;
    private String token;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
