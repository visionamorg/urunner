package com.runhub.feed.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runhub.feed.dto.CommentDto;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.model.Comment;
import com.runhub.feed.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedMapper {

    private final ObjectMapper objectMapper;

    public PostDto toPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setLikesCount(post.getLikesCount());
        dto.setCommentsCount(post.getCommentsCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setPostType(post.getPostType() != null ? post.getPostType() : "TEXT");

        if (post.getCommunity() != null) {
            dto.setCommunityId(post.getCommunity().getId());
        }

        if (post.getAuthor() != null) {
            dto.setAuthorId(post.getAuthor().getId());
            dto.setAuthorUsername(post.getAuthor().getDisplayUsername());
            dto.setAuthorProfileImageUrl(post.getAuthor().getProfileImageUrl());
            String firstName = post.getAuthor().getFirstName();
            String lastName = post.getAuthor().getLastName();
            String initials = "";
            if (firstName != null && !firstName.isEmpty()) initials += firstName.charAt(0);
            if (lastName != null && !lastName.isEmpty()) initials += lastName.charAt(0);
            dto.setAuthorInitials(initials.toUpperCase());
        }

        dto.setPinned(post.isPinned());
        dto.setDeleted(post.isDeleted());

        // Parse photoUrls from JSON string
        dto.setPhotoUrls(parsePhotoUrls(post.getPhotoUrls()));

        return dto;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getPost() != null) {
            dto.setPostId(comment.getPost().getId());
        }

        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setAuthorUsername(comment.getAuthor().getDisplayUsername());
            String firstName = comment.getAuthor().getFirstName();
            String lastName = comment.getAuthor().getLastName();
            String initials = "";
            if (firstName != null && !firstName.isEmpty()) initials += firstName.charAt(0);
            if (lastName != null && !lastName.isEmpty()) initials += lastName.charAt(0);
            dto.setAuthorInitials(initials.toUpperCase());
        }

        return dto;
    }

    public List<String> parsePhotoUrls(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse photoUrls JSON: {}", json, e);
            return Collections.emptyList();
        }
    }

    public String serializePhotoUrls(List<String> photoUrls) {
        if (photoUrls == null || photoUrls.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(photoUrls);
        } catch (Exception e) {
            log.warn("Failed to serialize photoUrls", e);
            return null;
        }
    }
}
