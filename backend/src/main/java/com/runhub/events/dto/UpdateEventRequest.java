package com.runhub.events.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateEventRequest {
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Double distanceKm;
    private BigDecimal price;
    private Integer maxParticipants;
    private List<String> photoUrls;
}
