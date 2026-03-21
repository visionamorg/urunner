package com.runhub.communities.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateGoalRequest {
    private String title;
    private Double targetKm;
    private LocalDate startDate;
    private LocalDate endDate;
}
