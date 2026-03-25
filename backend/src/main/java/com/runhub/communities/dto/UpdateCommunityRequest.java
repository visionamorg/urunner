package com.runhub.communities.dto;

import lombok.Data;

@Data
public class UpdateCommunityRequest {
    private String name;
    private String description;
    private String driveFolderId;
    private String coverUrl;
    private String imageUrl;
    private Boolean isPremium;
    private String stripePaymentUrl;
}
