package com.runhub.running.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ActivityDto {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private Double distanceKm;
    private Integer durationMinutes;
    private Double paceMinPerKm;
    private LocalDate activityDate;
    private String location;
    private String notes;
    private LocalDateTime createdAt;
}
