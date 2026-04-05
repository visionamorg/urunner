package com.runhub.communities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommunityDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String coverUrl;
    private String driveFolderId;
    private Boolean isPrivate;
    private Long creatorId;
    private String creatorUsername;
    private Integer memberCount;
    private LocalDateTime createdAt;
    private boolean joined;
    private String role;
    @JsonProperty("isAdmin")
    private boolean isAdmin;
    private int pendingInviteCount;
    private Boolean isPremium;
    private String stripePaymentUrl;
    private List<SponsorDto> sponsors;
    private String leaderboardMetric;
}
