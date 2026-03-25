package com.runhub.events.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GalleryPhotoDto {
    private Long id;
    private String photoUrl;
    private String thumbnailUrl;
    private String uploadedByUsername;
    private String bibNumber;
    private String taggedUsername;
    private LocalDateTime createdAt;
}
