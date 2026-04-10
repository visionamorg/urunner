package com.runhub.segments.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SegmentDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal distanceKm;
    private String difficulty;
    private String komUsername;
    private Integer komElapsedSeconds;
    private Integer myBestSeconds;
    private Long myRank;
}
