package com.runhub.running.dto;

import lombok.Data;

import java.util.List;

@Data
public class PushAthletesRequest {
    private List<Long> athleteIds; // empty = push to all active athletes
    private String scheduledDate;  // YYYY-MM-DD
}
