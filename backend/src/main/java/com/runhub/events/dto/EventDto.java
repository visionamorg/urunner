package com.runhub.events.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Double distanceKm;
    private BigDecimal price;
    private Integer maxParticipants;
    private Long organizerId;
    private String organizerUsername;
    private Long communityId;
    private String communityName;
    private Long participantCount;
    private LocalDateTime createdAt;
}
