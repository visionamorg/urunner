package com.runhub.users.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String bio;
    private String profileImageUrl;
}
