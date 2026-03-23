package com.runhub.ai.service;

import com.runhub.ai.dto.ActivityChatRequest;
import com.runhub.ai.dto.ActivityInsightDto;
import com.runhub.ai.model.ActivityInsight;
import com.runhub.ai.repository.ActivityInsightRepository;
import com.runhub.running.dto.ActivityStatsDto;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.running.service.ActivityService;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAIService {

    private final ActivityRepository activityRepository;
    private final ActivityInsightRepository insightRepository;
    private final ActivityService activityService;
    private final UserService userService;

    @Value("${application.ai.stitch-api-key:}")
    private String apiKey;

    @Value("${application.ai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${application.ai.model:gpt-4o-mini}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            You are an Expert Marathon Coach and Running Analyst embedded in a running tracking app called RunHub.
            You analyze running activities with deep expertise and provide actionable, personalized advice.

            Your responses should be:
            - Warm but professional, like a trusted coach
            - Data-driven: reference the actual numbers from the runner's activity
            - Actionable: give specific, practical advice
            - Concise: no filler, every sentence should add value

            When analyzing a run, consider: pace consistency, distance appropriateness,
            training load, recovery needs, and any notes the runner left about how they felt.
            """;

    public ActivityInsightDto analyzeActivity(Long activityId, String userEmail) {
        User user = userService.getUserEntityByEmail(userEmail);
        RunningActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Delete existing insight if re-analyzing
        insightRepository.findByActivityId(activityId).ifPresent(insightRepository::delete);

        ActivityStatsDto stats = activityService.getUserStats(userEmail);
        String context = buildActivityContext(activity, stats);

        String analyzePrompt = """
                Analyze this running activity and provide a JSON response with these exact fields:
                {
                  "summaryText": "A 2-3 sentence coach's analysis of this run — what went well, what to watch",
                  "intensity": "One of: EASY, MODERATE, TEMPO, THRESHOLD, HARD, RACE",
                  "nextRunSuggestion": "Specific suggestion for the next training session based on this run and recent volume",
                  "injuryRiskNotes": "Any injury risk flags based on the pace/distance/notes, or 'No concerns' if all looks good",
                  "socialCaption": "A short, inspiring social media caption for sharing this run (1-2 sentences, can be witty)"
                }

                IMPORTANT: Return ONLY the JSON object, no markdown, no code blocks, no extra text.

                """ + context;

        String aiResponse = callAI(SYSTEM_PROMPT, analyzePrompt);
        ActivityInsight insight = parseInsightResponse(activityId, aiResponse);
        insight = insightRepository.save(insight);
        return toDto(insight);
    }

    public Optional<ActivityInsightDto> getCachedInsight(Long activityId, String userEmail) {
        User user = userService.getUserEntityByEmail(userEmail);
        RunningActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return insightRepository.findByActivityId(activityId).map(this::toDto);
    }

    public String chatAboutActivity(Long activityId, String userEmail, ActivityChatRequest request) {
        User user = userService.getUserEntityByEmail(userEmail);
        RunningActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        ActivityStatsDto stats = activityService.getUserStats(userEmail);
        String context = buildActivityContext(activity, stats);

        String chatSystemPrompt = SYSTEM_PROMPT + "\n\nCurrent activity context:\n" + context;

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", chatSystemPrompt));

        if (request.getHistory() != null) {
            for (ActivityChatRequest.ChatMessage msg : request.getHistory()) {
                messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
            }
        }

        messages.add(Map.of("role", "user", "content", request.getMessage()));

        return callAIWithMessages(messages);
    }

    private String buildActivityContext(RunningActivity activity, ActivityStatsDto stats) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("=== ACTIVITY DATA ===\n");
        ctx.append("Title: ").append(activity.getTitle()).append("\n");
        ctx.append("Date: ").append(activity.getActivityDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");
        ctx.append("Distance: ").append(String.format("%.2f km", activity.getDistanceKm())).append("\n");
        ctx.append("Duration: ").append(activity.getDurationMinutes()).append(" minutes\n");
        if (activity.getPaceMinPerKm() != null) {
            int min = (int) Math.floor(activity.getPaceMinPerKm());
            int sec = (int) Math.round((activity.getPaceMinPerKm() - min) * 60);
            ctx.append("Pace: ").append(min).append(":").append(String.format("%02d", sec)).append(" /km\n");
        }
        if (activity.getLocation() != null && !activity.getLocation().isBlank()) {
            ctx.append("Location: ").append(activity.getLocation()).append("\n");
        }
        if (activity.getNotes() != null && !activity.getNotes().isBlank()) {
            ctx.append("Runner's Notes: ").append(activity.getNotes()).append("\n");
        }

        ctx.append("\n=== RUNNER'S HISTORICAL STATS ===\n");
        ctx.append("Total Runs: ").append(stats.getTotalRuns()).append("\n");
        ctx.append("Total Distance: ").append(String.format("%.1f km", stats.getTotalDistanceKm())).append("\n");
        ctx.append("Weekly Distance: ").append(String.format("%.1f km", stats.getWeeklyDistanceKm())).append("\n");
        ctx.append("Monthly Distance: ").append(String.format("%.1f km", stats.getMonthlyDistanceKm())).append("\n");
        if (stats.getAvgPaceMinPerKm() != null && stats.getAvgPaceMinPerKm() > 0) {
            int min = (int) Math.floor(stats.getAvgPaceMinPerKm());
            int sec = (int) Math.round((stats.getAvgPaceMinPerKm() - min) * 60);
            ctx.append("Average Pace: ").append(min).append(":").append(String.format("%02d", sec)).append(" /km\n");
        }

        return ctx.toString();
    }

    private String callAI(String systemPrompt, String userPrompt) {
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        );
        return callAIWithMessages(messages);
    }

    @SuppressWarnings("unchecked")
    private String callAIWithMessages(List<Map<String, String>> messages) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("STITCH_API_KEY not configured, returning fallback response");
            return getFallbackResponse();
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", messages);
            body.put("temperature", 0.7);
            body.put("max_tokens", 1000);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            log.error("AI API call failed: {}", e.getMessage());
        }

        return getFallbackResponse();
    }

    private String getFallbackResponse() {
        return """
                {"summaryText":"AI analysis is currently unavailable. Please check your API key configuration.","intensity":"MODERATE","nextRunSuggestion":"Continue with your regular training schedule.","injuryRiskNotes":"No concerns","socialCaption":"Another run in the books! Keep moving forward."}""";
    }

    private ActivityInsight parseInsightResponse(Long activityId, String response) {
        String clean = response.strip();
        if (clean.startsWith("```")) {
            clean = clean.replaceAll("^```[a-z]*\\n?", "").replaceAll("\\n?```$", "").strip();
        }

        ActivityInsight.ActivityInsightBuilder builder = ActivityInsight.builder().activityId(activityId);

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, String> parsed = mapper.readValue(clean, Map.class);
            builder.summaryText(parsed.getOrDefault("summaryText", "Analysis complete."));
            builder.intensity(parsed.getOrDefault("intensity", "MODERATE"));
            builder.nextRunSuggestion(parsed.getOrDefault("nextRunSuggestion", ""));
            builder.injuryRiskNotes(parsed.getOrDefault("injuryRiskNotes", "No concerns"));
            builder.socialCaption(parsed.getOrDefault("socialCaption", ""));
        } catch (Exception e) {
            log.warn("Failed to parse AI JSON response, using raw text: {}", e.getMessage());
            builder.summaryText(clean);
            builder.intensity("MODERATE");
        }

        return builder.build();
    }

    private ActivityInsightDto toDto(ActivityInsight insight) {
        return ActivityInsightDto.builder()
                .id(insight.getId())
                .activityId(insight.getActivityId())
                .summaryText(insight.getSummaryText())
                .intensity(insight.getIntensity())
                .nextRunSuggestion(insight.getNextRunSuggestion())
                .injuryRiskNotes(insight.getInjuryRiskNotes())
                .socialCaption(insight.getSocialCaption())
                .createdAt(insight.getCreatedAt())
                .build();
    }
}
