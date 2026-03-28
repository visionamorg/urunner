package com.runhub.programs.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProgramRequest {
    private String name;
    private String description;
    private String level;
    private Integer durationWeeks;
    private Double targetDistanceKm;
    private BigDecimal price;
    private List<ProgramSessionDto> sessions;
}
