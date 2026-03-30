package com.runhub.coaching.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CoachingCommentDto {
    private Long id;
    private Long coachId;
    private String coachUsername;
    private Long activityId;
    private String content;
    private Integer rating;
    private Integer lapNumber;
    private Boolean pinnedToAthleteDashboard;
    private LocalDateTime createdAt;
}
