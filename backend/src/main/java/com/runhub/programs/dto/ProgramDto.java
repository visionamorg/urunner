package com.runhub.programs.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgramDto {
    private Long id;
    private String name;
    private String description;
    private String level;
    private Integer durationWeeks;
    private Double targetDistanceKm;
    private Integer sessionsCount;
    private LocalDateTime createdAt;
}
