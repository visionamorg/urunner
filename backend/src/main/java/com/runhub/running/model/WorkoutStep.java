package com.runhub.running.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkoutStep {
    private Integer order;
    // WARMUP, INTERVAL, RECOVERY, REST, COOLDOWN, REPEAT
    private String stepType;
    // TIME (ms), DISTANCE (meters), OPEN, LAP_BUTTON
    private String durationUnit;
    private Long durationValue;
    // NO_TARGET, PACE, HEART_RATE, CADENCE, POWER, SPEED
    private String targetType;
    // PACE: sec/km; HEART_RATE: bpm; CADENCE: rpm
    private Double targetLow;
    private Double targetHigh;
    private String notes;
    private Integer repeatCount;
    private List<WorkoutStep> children;
}
