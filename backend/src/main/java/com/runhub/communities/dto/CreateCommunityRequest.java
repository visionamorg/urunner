package com.runhub.communities.dto;

import lombok.Data;

@Data
public class CreateCommunityRequest {
    private String name;
    private String description;
    private String imageUrl;
}
