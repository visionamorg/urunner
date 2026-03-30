package com.runhub.running.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetricDto {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Integer restingHeartRate;
    private Integer sleepScore;
    private Double vo2Max;
    private Integer fitnessAge;
    private String hrvStatus;
    private Integer bodyBatteryMax;
    private Integer stressLevel;
}
