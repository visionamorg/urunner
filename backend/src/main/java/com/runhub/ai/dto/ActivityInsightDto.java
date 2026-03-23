package com.runhub.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityInsightDto {
    private Long id;
    private Long activityId;
    private String summaryText;
    private String intensity;
    private String nextRunSuggestion;
    private String injuryRiskNotes;
    private String socialCaption;
    private LocalDateTime createdAt;
}
