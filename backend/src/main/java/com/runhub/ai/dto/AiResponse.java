package com.runhub.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiResponse {
    private String answer;
    private List<String> tips;
}
