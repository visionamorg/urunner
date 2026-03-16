package com.runhub.feed.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private Long authorId;
    private String authorUsername;
    private String authorProfileImageUrl;
    private String content;
    private String imageUrl;
    private Long communityId;
    private Integer likesCount;
    private Integer commentsCount;
    private LocalDateTime createdAt;
    private boolean likedByCurrentUser;
}
