package com.runhub.coaching.dto;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private Long activityId;
    private String content;
    private Integer rating;
    private Integer lapNumber;
}
