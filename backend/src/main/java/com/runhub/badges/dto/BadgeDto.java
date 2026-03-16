package com.runhub.badges.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BadgeDto {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private String criteria;
    private LocalDateTime createdAt;
}
