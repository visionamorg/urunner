package com.runhub.running.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    // Telemetry
    private Integer elevationGainMeters;
    private Integer avgHeartRate;
    private Integer maxHeartRate;
    private Integer avgCadence;
    private String mapPolyline;
    private List<ActivitySplitDto> splits;
}
