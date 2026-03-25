package com.runhub.programs.dto;

import lombok.Data;

@Data
public class EnrolleeProgressDto {
    private String username;
    private Integer completedSessions;
    private Integer totalSessions;
    private String status;
}
