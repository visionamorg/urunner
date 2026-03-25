package com.runhub.running.dto;

import lombok.Data;

@Data
public class ActivitySplitDto {
    private Integer splitKm;
    private Double splitPace;
    private Double splitElevation;
    private Integer splitHeartRate;
}
