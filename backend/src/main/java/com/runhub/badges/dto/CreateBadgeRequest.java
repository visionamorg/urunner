package com.runhub.badges.dto;

import lombok.Data;

@Data
public class CreateBadgeRequest {
    private String name;
    private String description;
    private String iconUrl;
    private String criteria;
}
