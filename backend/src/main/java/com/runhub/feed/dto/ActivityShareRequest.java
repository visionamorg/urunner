package com.runhub.feed.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ActivityShareRequest {
    private Long activityId;
    private String caption;
    private Long communityId;
}
