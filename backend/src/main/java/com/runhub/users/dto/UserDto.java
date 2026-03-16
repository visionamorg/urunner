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
}
