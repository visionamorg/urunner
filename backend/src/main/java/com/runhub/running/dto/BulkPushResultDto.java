package com.runhub.running.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkPushResultDto {

    private Long sessionId;
    private String sessionTitle;
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
