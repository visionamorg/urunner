package com.runhub.running.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StreakDto {
    private int currentStreak;
    private int longestStreak;
    private boolean activeToday;
    private int totalActiveDays;
}
