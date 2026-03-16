package com.runhub.running.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateActivityRequest {
    private String title;
    private Double distanceKm;
    private Integer durationMinutes;
    private LocalDate activityDate;
    private String location;
    private String notes;
}
