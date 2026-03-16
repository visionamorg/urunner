package com.runhub.users.dto;

import com.runhub.users.model.AuthProvider;
import com.runhub.users.model.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String profileImageUrl;
    private Role role;
    private AuthProvider authProvider;
    private LocalDateTime createdAt;
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
