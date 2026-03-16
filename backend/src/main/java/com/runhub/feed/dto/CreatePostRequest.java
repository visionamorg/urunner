package com.runhub.feed.dto;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String content;
    private String imageUrl;
    private Long communityId;
}
