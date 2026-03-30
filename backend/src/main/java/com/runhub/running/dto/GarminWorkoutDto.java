package com.runhub.running.dto;

import com.runhub.running.model.WorkoutStep;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GarminWorkoutDto {
    private Long id;
    private String title;
    private String sport;
    private String description;
    private List<WorkoutStep> steps;
    private boolean template;
    private int stepCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
