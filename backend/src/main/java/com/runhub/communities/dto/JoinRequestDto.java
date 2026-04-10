package com.runhub.communities.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class JoinRequestDto {
    private Long id;
    private Long userId;
    private String username;
    private String profileImageUrl;
    private String status;
    private LocalDateTime createdAt;
}
