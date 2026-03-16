package com.runhub.feed.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    private Long id;
    private Long authorId;
    private String authorUsername;
    private String authorProfileImageUrl;
    private String authorInitials;
    private Long communityId;
    private String postType;
    private String content;
    private String imageUrl;
    private List<String> photoUrls;
    private Integer likesCount;
    private Integer commentsCount;
    private boolean liked;
    private boolean likedByCurrentUser;
    private boolean pinned;
    private boolean deleted;
    private LocalDateTime createdAt;
    private List<CommentDto> comments;
}
