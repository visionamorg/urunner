package com.runhub.programs.dto;

import lombok.Data;

@Data
public class GeneratePlanRequest {
    private String goalType;       // MARATHON, HALF_MARATHON, 10K, 5K, BASE_BUILDING
    private String targetTime;     // e.g. "3:45:00" or null if no target
    private Integer durationWeeks; // 8, 12, 16, 20
    private Integer daysPerWeek;   // 3-6
    private Double currentWeeklyKm; // optional override, otherwise auto-detected
}
