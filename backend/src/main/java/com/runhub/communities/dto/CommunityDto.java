package com.runhub.communities.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long creatorId;
    private String creatorUsername;
    private Integer memberCount;
    private LocalDateTime createdAt;
}
