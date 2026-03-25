package com.runhub.communities.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityTagDto {
    private Long id;
    private String name;
    private String color;
}
