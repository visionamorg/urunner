package com.runhub.programs.dto;

import lombok.Data;

@Data
public class ProgramSessionDto {
    private Long id;
    private Integer weekNumber;
    private Integer dayNumber;
    private String title;
    private String description;
    private Double distanceKm;
    private Integer durationMinutes;
}
