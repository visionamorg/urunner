package com.runhub.users.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String bio;
    private String profileImageUrl;
    private String location;
    private String runningCategory;
    private String passion;
    private String gender;
    private Integer yearsRunning;
    private Double weeklyGoalKm;
    private String pb5k;
    private String pb10k;
    private String pbHalfMarathon;
    private String pbMarathon;
    private String instagramHandle;
}
