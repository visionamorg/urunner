package com.runhub.programs.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgramProgressDto {
    private Long id;
    private Long programId;
    private String programName;
    private String programLevel;
    private Integer durationWeeks;
    private LocalDateTime startedAt;
    private Integer completedSessions;
    private Integer totalSessions;
    private String status;
}
