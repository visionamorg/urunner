package com.runhub.communities.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ActiveChallengeDto {
    private Long id;
    private String title;
    private String description;
    private String targetType;
    private Double targetValue;
    private Double currentValue;
    private String startDate;
    private String endDate;
    private String status;
    private Integer participantCount;
    private Double progressPercent;
    private List<MemberProgressDto> memberProgress;

    @Data
    @Builder
    public static class MemberProgressDto {
        private Long userId;
        private String username;
        private String profileImageUrl;
        private Double contribution;
        private Double progressPercent;
    }
}
