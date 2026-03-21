package com.runhub.communities.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CommunityGoalDto {
    private Long id;
    private String title;
    private Double targetKm;
    private Double progressKm;
    private Double progressPercent;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private boolean completed;
}
