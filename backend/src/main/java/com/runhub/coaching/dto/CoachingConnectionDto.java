package com.runhub.coaching.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CoachingConnectionDto {
    private Long id;
    private Long coachId;
    private String coachUsername;
    private Long athleteId;
    private String athleteUsername;
    private String athleteProfileImageUrl;
    private String garminAccessLevel;
    private String status;
    private String inviteToken;
    private LocalDateTime createdAt;
}
