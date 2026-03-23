package com.runhub.ai.controller;

import com.runhub.ai.dto.ActivityChatRequest;
import com.runhub.ai.dto.ActivityChatResponse;
import com.runhub.ai.dto.ActivityInsightDto;
import com.runhub.ai.service.ActivityAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityAIController {

    private final ActivityAIService activityAIService;

    @PostMapping("/{id}/analyze")
    public ResponseEntity<ActivityInsightDto> analyzeActivity(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(activityAIService.analyzeActivity(id, principal.getName()));
    }

    @GetMapping("/{id}/insight")
    public ResponseEntity<ActivityInsightDto> getInsight(@PathVariable Long id, Principal principal) {
        return activityAIService.getCachedInsight(id, principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/chat")
    public ResponseEntity<ActivityChatResponse> chatAboutActivity(
            @PathVariable Long id,
            @RequestBody ActivityChatRequest request,
            Principal principal) {
        String reply = activityAIService.chatAboutActivity(id, principal.getName(), request);
        return ResponseEntity.ok(ActivityChatResponse.builder().reply(reply).build());
    }
}
