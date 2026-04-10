package com.runhub.running.service;

import com.runhub.ai.service.ActivityAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityPostProcessingService {

    private final ActivityAIService activityAIService;

    @Async
    public void runAiSummary(Long activityId, String email) {
        try {
            log.info("Async AI summary triggered for activity {}", activityId);
            activityAIService.analyzeActivity(activityId, email);
        } catch (Exception e) {
            log.warn("Async AI summary skipped for activity {}: {}", activityId, e.getMessage());
        }
    }
}
