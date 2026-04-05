package com.runhub.communities.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntryDto {
    private Integer rank;
    private Long userId;
    private String username;
    private String profileImageUrl;
    private Double totalDistanceKm;
    private Long totalRuns;
    private Long totalDurationMinutes;
    private Long totalElevationMeters;
    private Double value;
    private String metric;
}
