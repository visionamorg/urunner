package com.runhub.events.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Double distanceKm;
    private BigDecimal price;
    private Integer maxParticipants;
}
