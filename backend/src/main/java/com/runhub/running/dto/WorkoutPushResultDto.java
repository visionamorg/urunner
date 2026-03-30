package com.runhub.running.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkoutPushResultDto {
    private Long workoutId;
    private String workoutTitle;
    private List<AthleteResult> results;

    @Data
    @Builder
    public static class AthleteResult {
        private Long athleteId;
        private String username;
        private boolean success;
        private String garminWorkoutId;
        private String error;
    }
}
