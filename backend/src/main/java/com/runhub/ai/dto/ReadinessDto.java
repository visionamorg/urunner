package com.runhub.ai.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReadinessDto {
    private int score;               // 0-100
    private String level;            // HIGH, MODERATE, LOW, CRITICAL
    private String recommendation;   // AI-generated advice
    private Double weeklyVolumeKm;
    private Double previousWeekVolumeKm;
    private Double volumeChangePercent;
    private Double avgEfficiencyFactor; // pace per heartbeat
    private int runsLast7Days;
    private int restDaysSinceLast;
}
