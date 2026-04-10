package com.runhub.users.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PublicProfileDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String bio;
    private String location;
    private String runningCategory;
    private String pb5k;
    private String pb10k;
    private String pbHalfMarathon;
    private String pbMarathon;
    private long followerCount;
    private long followingCount;
    private boolean following;
    private Double totalKm;
    private Long totalRuns;
    private List<Object> recentActivities;
}
