package com.runhub.badges.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBadgeDto {
    private Long badgeId;
    private String badgeName;
    private String badgeDescription;
    private String badgeIconUrl;
    private LocalDateTime earnedAt;
}
