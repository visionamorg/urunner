package com.runhub.communities.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommunityMemberDto {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String role;
    private LocalDateTime joinedAt;
    private String initials;
    private List<CommunityTagDto> tags = new ArrayList<>();
    private LocalDateTime lastRunDate;
    private Long messageCount30d;
    private Boolean leaderboardOptOut;
}
