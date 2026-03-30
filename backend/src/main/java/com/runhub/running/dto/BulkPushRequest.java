package com.runhub.running.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkPushRequest {
    private Long sessionId;
    private List<Long> athleteIds;
    /** Optional ISO date YYYY-MM-DD to schedule the workout */
    private String scheduledDate;
}
