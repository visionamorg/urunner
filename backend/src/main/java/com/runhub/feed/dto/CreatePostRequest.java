package com.runhub.feed.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequest {
    private String content;
    private String imageUrl;
    private Long communityId;
    private String postType = "TEXT";
    private List<String> photoUrls;
}
