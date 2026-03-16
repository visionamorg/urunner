package com.runhub.rooms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomDto {
    private Long id;
    private String name;
    private String description;
    private Long communityId;
    private String createdByUsername;
    private Boolean isPrivate;
    private LocalDateTime createdAt;
    private Long memberCount;
}
