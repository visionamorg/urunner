package com.runhub.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PerformanceDto {
    private double currentCTL;    // Chronic Training Load (fitness, 42-day EWMA)
    private double currentATL;    // Acute Training Load (fatigue, 7-day EWMA)
    private double currentTSB;    // Training Stress Balance (form = CTL - ATL)
    private String trainingZone;  // OPTIMAL, OVERREACHING, RECOVERY, DETRAINING
    private List<DailyMetric> history;
    private List<DailyMetric> taperSimulation; // simulated 7 days of rest

    @Data
    @Builder
    public static class DailyMetric {
        private LocalDate date;
        private double tss;
        private double ctl;
        private double atl;
        private double tsb;
    }
}
