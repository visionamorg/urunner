package com.runhub.feed.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long postId;
    private Long authorId;
    private String authorUsername;
    private String authorInitials;
    private String content;
    private LocalDateTime createdAt;
}
