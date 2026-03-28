package com.runhub.ai.service;

import com.runhub.ai.dto.ReadinessDto;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadinessService {

    private final ActivityRepository activityRepository;
    private final UserService userService;

    @Value("${application.ai.stitch-api-key:}")
    private String apiKey;

    @Value("${application.ai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${application.ai.model:gpt-4o-mini}")
    private String model;

    public ReadinessDto getReadiness(String userEmail) {
        User user = userService.getUserEntityByEmail(userEmail);
        List<RunningActivity> allActivities = activityRepository.findByUserIdOrderByActivityDateDesc(user.getId());

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);
        LocalDate fourteenDaysAgo = today.minusDays(14);

        // This week's activities (last 7 days)
        List<RunningActivity> thisWeek = allActivities.stream()
                .filter(a -> a.getActivityDate() != null && !a.getActivityDate().isBefore(sevenDaysAgo))
                .toList();

        // Previous week's activities (7-14 days ago)
        List<RunningActivity> lastWeek = allActivities.stream()
                .filter(a -> a.getActivityDate() != null
                        && !a.getActivityDate().isBefore(fourteenDaysAgo)
                        && a.getActivityDate().isBefore(sevenDaysAgo))
                .toList();

        double thisWeekKm = thisWeek.stream()
                .mapToDouble(a -> a.getDistanceKm() != null ? a.getDistanceKm() : 0).sum();
        double lastWeekKm = lastWeek.stream()
                .mapToDouble(a -> a.getDistanceKm() != null ? a.getDistanceKm() : 0).sum();

        double volumeChange = lastWeekKm > 0 ? ((thisWeekKm - lastWeekKm) / lastWeekKm) * 100 : 0;

        // Efficiency factor: average (pace / avgHR) for activities with HR data
        OptionalDouble avgEF = thisWeek.stream()
                .filter(a -> a.getAvgHeartRate() != null && a.getAvgHeartRate() > 0
                        && a.getPaceMinPerKm() != null && a.getPaceMinPerKm() > 0)
                .mapToDouble(a -> a.getPaceMinPerKm() / a.getAvgHeartRate())
                .average();

        // Rest days since last activity
        int restDaysSinceLast = 0;
        if (!allActivities.isEmpty() && allActivities.get(0).getActivityDate() != null) {
            restDaysSinceLast = (int) ChronoUnit.DAYS.between(allActivities.get(0).getActivityDate(), today);
        }

        // Calculate readiness score (0-100)
        int score = calculateScore(thisWeek.size(), volumeChange, avgEF, restDaysSinceLast, thisWeekKm);

        String level;
        if (score >= 70) level = "HIGH";
        else if (score >= 50) level = "MODERATE";
        else if (score >= 30) level = "LOW";
        else level = "CRITICAL";

        String recommendation = generateRecommendation(score, level, thisWeekKm, lastWeekKm, volumeChange,
                thisWeek.size(), restDaysSinceLast, avgEF);

        return ReadinessDto.builder()
                .score(score)
                .level(level)
                .recommendation(recommendation)
                .weeklyVolumeKm(Math.round(thisWeekKm * 10.0) / 10.0)
                .previousWeekVolumeKm(Math.round(lastWeekKm * 10.0) / 10.0)
                .volumeChangePercent(Math.round(volumeChange * 10.0) / 10.0)
                .avgEfficiencyFactor(avgEF.isPresent() ? Math.round(avgEF.getAsDouble() * 1000.0) / 1000.0 : null)
                .runsLast7Days(thisWeek.size())
                .restDaysSinceLast(restDaysSinceLast)
                .build();
    }

    private int calculateScore(int runsThisWeek, double volumeChange, OptionalDouble avgEF,
                               int restDays, double weeklyKm) {
        int score = 70; // baseline

        // Volume spike penalty (10% rule)
        if (volumeChange > 20) score -= 25;
        else if (volumeChange > 10) score -= 15;
        else if (volumeChange > 0) score -= 5;
        else if (volumeChange < -20) score += 5; // recovery week bonus

        // Too many consecutive days without rest
        if (runsThisWeek >= 7) score -= 15;
        else if (runsThisWeek >= 6) score -= 8;
        else if (runsThisWeek <= 2) score += 5;

        // Haven't run in a while (detraining)
        if (restDays >= 5) score -= 10;
        else if (restDays >= 3) score -= 5;
        else if (restDays == 0) score -= 3; // no rest today

        // Extremely high volume for the week
        if (weeklyKm > 80) score -= 10;
        else if (weeklyKm > 60) score -= 5;

        // No data means uncertain
        if (runsThisWeek == 0 && weeklyKm == 0) score = 50;

        return Math.max(0, Math.min(100, score));
    }

    @SuppressWarnings("unchecked")
    private String generateRecommendation(int score, String level, double thisWeekKm, double lastWeekKm,
                                          double volumeChange, int runs, int restDays, OptionalDouble ef) {
        // Try AI-powered recommendation
        if (apiKey != null && !apiKey.isBlank()) {
            try {
                String prompt = """
                        As a running coach, give ONE concise recommendation (2-3 sentences max) based on this runner's readiness data:
                        - Readiness Score: %d/100 (%s)
                        - Weekly distance: %.1f km (previous week: %.1f km, change: %+.1f%%)
                        - Runs this week: %d
                        - Rest days since last run: %d
                        %s

                        If score < 40: recommend rest or very easy recovery run.
                        If score 40-60: suggest lighter training, mention the 10%% rule if volume spiked.
                        If score > 60: green light for normal training.

                        Return ONLY the recommendation text, no JSON, no formatting.
                        """.formatted(score, level, thisWeekKm, lastWeekKm, volumeChange,
                        runs, restDays,
                        ef.isPresent() ? "- Efficiency Factor: " + String.format("%.3f", ef.getAsDouble()) : "- No heart rate data available");

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);

                Map<String, Object> body = new HashMap<>();
                body.put("model", model);
                body.put("messages", List.of(
                        Map.of("role", "system", "content", "You are a concise running coach. Keep responses under 3 sentences."),
                        Map.of("role", "user", "content", prompt)
                ));
                body.put("temperature", 0.7);
                body.put("max_tokens", 200);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
                ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

                if (response.getBody() != null) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return ((String) message.get("content")).strip();
                    }
                }
            } catch (Exception e) {
                log.warn("AI recommendation call failed, using fallback: {}", e.getMessage());
            }
        }

        // Fallback rule-based recommendations
        if (score < 30) return "Your body needs recovery. Take a rest day or do a very easy 20-minute walk. Your volume increased significantly — back off to prevent injury.";
        if (score < 50) return "Consider an easy recovery run today (max 30 min at conversational pace). Your training load is elevated, so prioritize sleep and hydration.";
        if (score < 70) return "You're in a moderate readiness state. Stick to your planned session but listen to your body — if something feels off, dial it back.";
        return "You're well-rested and ready to train! This is a good day for quality work like tempo or intervals if your plan calls for it.";
    }
}
