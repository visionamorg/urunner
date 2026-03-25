package com.runhub.programs.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateProgramRequest {
    private String name;
    private String description;
    private String level;
    private Integer durationWeeks;
    private Double targetDistanceKm;
    private List<ProgramSessionDto> sessions;
}
