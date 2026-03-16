package com.runhub.running.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityStatsDto {
    private Double totalDistanceKm;
    private Long totalRuns;
    private Long totalDurationMinutes;
    private Double avgPaceMinPerKm;
    private Double weeklyDistanceKm;
    private Double monthlyDistanceKm;
}
