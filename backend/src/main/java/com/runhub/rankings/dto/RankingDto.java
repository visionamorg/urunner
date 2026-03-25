package com.runhub.rankings.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankingDto {
    private Integer rank;
    private Long userId;
    private String username;
    private String profileImageUrl;
    private Double totalDistanceKm;
    private Long totalRuns;
    private Long totalDurationMinutes;
    private Long totalElevationMeters;
}
