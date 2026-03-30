package com.runhub.running.dto;

import com.runhub.running.model.WorkoutStep;
import lombok.Data;

import java.util.List;

@Data
public class CreateWorkoutRequest {
    private String title;
    private String sport;
    private String description;
    private List<WorkoutStep> steps;
    private boolean template;
}
