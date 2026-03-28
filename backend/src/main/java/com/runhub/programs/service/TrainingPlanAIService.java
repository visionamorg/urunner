package com.runhub.programs.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runhub.programs.dto.GeneratePlanRequest;
import com.runhub.programs.dto.ProgramDto;
import com.runhub.programs.mapper.ProgramMapper;
import com.runhub.programs.model.Program;
import com.runhub.programs.model.ProgramSession;
import com.runhub.programs.repository.ProgramRepository;
import com.runhub.programs.repository.ProgramSessionRepository;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingPlanAIService {

    private final ActivityRepository activityRepository;
    private final ProgramRepository programRepository;
    private final ProgramSessionRepository sessionRepository;
    private final ProgramMapper programMapper;
    private final UserService userService;

    @Value("${application.ai.stitch-api-key:}")
    private String apiKey;

    @Value("${application.ai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${application.ai.model:gpt-4o-mini}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            You are an Elite Running Coach with 20 years of experience building periodized training plans.
            You create scientifically-backed plans following the BASE → BUILD → PEAK → TAPER model.

            Rules:
            - Never increase weekly volume by more than 10% week-over-week
            - Include a recovery/cutback week every 3-4 weeks (reduce volume ~30%)
            - Easy runs should be 60-70% of total volume
            - Quality sessions (tempo, intervals, long run) are 30-40%
            - The last 2-3 weeks before race should be a taper (progressive volume reduction)
            - Rest days are real rest — no running
            """;

    @Transactional
    public ProgramDto generatePlan(GeneratePlanRequest request, String userEmail) {
        User user = userService.getUserEntityByEmail(userEmail);

        // Fetch last 5 activities to establish baseline
        List<RunningActivity> recentActivities = activityRepository
                .findByUserIdOrderByActivityDateDesc(user.getId())
                .stream().limit(10).toList();

        String runnerProfile = buildRunnerProfile(recentActivities, request);
        String prompt = buildGenerationPrompt(request, runnerProfile);

        String aiResponse = callAI(prompt);
        return parseAndSavePlan(aiResponse, request, user);
    }

    private String buildRunnerProfile(List<RunningActivity> activities, GeneratePlanRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RUNNER BASELINE ===\n");

        if (activities.isEmpty()) {
            sb.append("No recent activity data. Treat as a beginner runner.\n");
            sb.append("Estimated weekly volume: ").append(
                    request.getCurrentWeeklyKm() != null ? request.getCurrentWeeklyKm() + " km" : "15 km (default beginner)"
            ).append("\n");
            return sb.toString();
        }

        // Calculate stats from recent activities
        double totalDist = activities.stream().mapToDouble(a -> a.getDistanceKm() != null ? a.getDistanceKm() : 0).sum();
        double avgDist = totalDist / activities.size();
        OptionalDouble avgPace = activities.stream()
                .filter(a -> a.getPaceMinPerKm() != null && a.getPaceMinPerKm() > 0)
                .mapToDouble(RunningActivity::getPaceMinPerKm).average();

        // Estimate weekly volume from last 4 weeks
        LocalDate fourWeeksAgo = LocalDate.now().minusWeeks(4);
        double last4WeeksDist = activities.stream()
                .filter(a -> a.getActivityDate() != null && a.getActivityDate().isAfter(fourWeeksAgo))
                .mapToDouble(a -> a.getDistanceKm() != null ? a.getDistanceKm() : 0).sum();
        double weeklyAvg = last4WeeksDist / 4.0;

        sb.append("Recent runs analyzed: ").append(activities.size()).append("\n");
        sb.append("Average run distance: ").append(String.format("%.1f km", avgDist)).append("\n");
        if (avgPace.isPresent()) {
            int min = (int) Math.floor(avgPace.getAsDouble());
            int sec = (int) Math.round((avgPace.getAsDouble() - min) * 60);
            sb.append("Average pace: ").append(min).append(":").append(String.format("%02d", sec)).append(" /km\n");
        }
        sb.append("Estimated weekly volume: ").append(String.format("%.1f km", weeklyAvg)).append("\n");

        // List last 5 runs for context
        sb.append("\nLast 5 runs:\n");
        activities.stream().limit(5).forEach(a -> {
            sb.append("- ").append(a.getActivityDate()).append(": ")
                    .append(String.format("%.1f km", a.getDistanceKm() != null ? a.getDistanceKm() : 0));
            if (a.getPaceMinPerKm() != null) {
                int m = (int) Math.floor(a.getPaceMinPerKm());
                int s = (int) Math.round((a.getPaceMinPerKm() - m) * 60);
                sb.append(" @ ").append(m).append(":").append(String.format("%02d", s)).append("/km");
            }
            sb.append("\n");
        });

        return sb.toString();
    }

    private String buildGenerationPrompt(GeneratePlanRequest request, String runnerProfile) {
        String goalLabel = switch (request.getGoalType()) {
            case "MARATHON" -> "Marathon (42.195 km)";
            case "HALF_MARATHON" -> "Half Marathon (21.1 km)";
            case "10K" -> "10K";
            case "5K" -> "5K";
            default -> "General base building / fitness";
        };

        return """
                Generate a %d-week training plan for a runner with the following profile:

                %s

                GOAL: %s
                TARGET TIME: %s
                TRAINING DAYS PER WEEK: %d

                Return a JSON array of workout objects. Each object must have exactly these fields:
                {
                  "weekNumber": <int 1-%d>,
                  "dayNumber": <int 1-7, Monday=1 Sunday=7>,
                  "title": "<short workout title, e.g. 'Easy Run', 'Tempo Run', 'Long Run', 'Intervals', 'Rest Day'>",
                  "description": "<1-2 sentence description with specific paces or zones if applicable>",
                  "distanceKm": <number or 0 for rest>,
                  "durationMinutes": <estimated minutes or 0 for rest>
                }

                IMPORTANT RULES:
                - Return ONLY the JSON array, no markdown, no code blocks, no explanation
                - Include exactly %d training days per week (remaining days are rest — do NOT include rest day entries)
                - Phase the plan: BASE (weeks 1-%d), BUILD (weeks %d-%d), PEAK (weeks %d-%d), TAPER (last %d weeks)
                - Respect the 10%% weekly volume increase rule
                - Add a cutback week (30%% less volume) every 3rd or 4th week
                """.formatted(
                request.getDurationWeeks(),
                runnerProfile,
                goalLabel,
                request.getTargetTime() != null ? request.getTargetTime() : "No specific target — focus on completion",
                request.getDaysPerWeek(),
                request.getDurationWeeks(),
                request.getDaysPerWeek(),
                // Phase calculations
                Math.max(1, request.getDurationWeeks() / 4),
                Math.max(2, request.getDurationWeeks() / 4) + 1,
                Math.max(3, request.getDurationWeeks() * 2 / 4),
                Math.max(3, request.getDurationWeeks() * 2 / 4) + 1,
                Math.max(4, request.getDurationWeeks() * 3 / 4),
                Math.max(2, request.getDurationWeeks() / 6)
        );
    }

    @SuppressWarnings("unchecked")
    private String callAI(String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI API key not configured, returning fallback plan");
            return getFallbackPlan();
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", SYSTEM_PROMPT),
                    Map.of("role", "user", "content", userPrompt)
            ));
            body.put("temperature", 0.7);
            body.put("max_tokens", 4000);

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
            log.error("AI API call failed for plan generation: {}", e.getMessage());
        }

        return getFallbackPlan();
    }

    private ProgramDto parseAndSavePlan(String aiResponse, GeneratePlanRequest request, User user) {
        String clean = aiResponse.strip();
        if (clean.startsWith("```")) {
            clean = clean.replaceAll("^```[a-z]*\\n?", "").replaceAll("\\n?```$", "").strip();
        }

        String goalLabel = switch (request.getGoalType()) {
            case "MARATHON" -> "Marathon";
            case "HALF_MARATHON" -> "Half Marathon";
            case "10K" -> "10K";
            case "5K" -> "5K";
            default -> "Base Building";
        };

        String level;
        if (request.getDurationWeeks() <= 8) level = "BEGINNER";
        else if (request.getDurationWeeks() <= 14) level = "INTERMEDIATE";
        else level = "ADVANCED";

        double targetDist = switch (request.getGoalType()) {
            case "MARATHON" -> 42.195;
            case "HALF_MARATHON" -> 21.1;
            case "10K" -> 10.0;
            case "5K" -> 5.0;
            default -> 0;
        };

        String planName = "AI " + goalLabel + " Plan";
        if (request.getTargetTime() != null && !request.getTargetTime().isBlank()) {
            planName += " (" + request.getTargetTime() + ")";
        }
        planName += " — " + request.getDurationWeeks() + "w";

        Program program = Program.builder()
                .name(planName)
                .description("AI-generated " + request.getDurationWeeks() + "-week " + goalLabel.toLowerCase()
                        + " training plan. " + request.getDaysPerWeek() + " days/week.")
                .level(level)
                .durationWeeks(request.getDurationWeeks())
                .targetDistanceKm(targetDist > 0 ? targetDist : null)
                .createdBy(user)
                .sessions(new ArrayList<>())
                .build();
        program = programRepository.save(program);

        // Parse sessions from AI
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> sessions = mapper.readValue(clean, new TypeReference<>() {});
            for (Map<String, Object> s : sessions) {
                ProgramSession session = ProgramSession.builder()
                        .program(program)
                        .weekNumber(toInt(s.get("weekNumber"), 1))
                        .dayNumber(toInt(s.get("dayNumber"), 1))
                        .title(String.valueOf(s.getOrDefault("title", "Workout")))
                        .description(String.valueOf(s.getOrDefault("description", "")))
                        .distanceKm(toDouble(s.get("distanceKm")))
                        .durationMinutes(toInt(s.get("durationMinutes"), 0))
                        .build();
                sessionRepository.save(session);
                program.getSessions().add(session);
            }
        } catch (Exception e) {
            log.error("Failed to parse AI plan response, generating fallback sessions: {}", e.getMessage());
            generateFallbackSessions(program, request);
        }

        ProgramDto dto = programMapper.toDto(program);
        dto.setSessionsCount(program.getSessions().size());
        return dto;
    }

    private void generateFallbackSessions(Program program, GeneratePlanRequest request) {
        for (int week = 1; week <= request.getDurationWeeks(); week++) {
            for (int day = 1; day <= request.getDaysPerWeek(); day++) {
                String title;
                double dist;
                if (day == request.getDaysPerWeek()) {
                    title = "Long Run";
                    dist = 8 + (week * 0.5);
                } else if (day == Math.max(1, request.getDaysPerWeek() / 2)) {
                    title = "Tempo Run";
                    dist = 5 + (week * 0.3);
                } else {
                    title = "Easy Run";
                    dist = 4 + (week * 0.2);
                }

                ProgramSession session = ProgramSession.builder()
                        .program(program)
                        .weekNumber(week)
                        .dayNumber(day)
                        .title(title)
                        .description("Week " + week + " - " + title)
                        .distanceKm(Math.round(dist * 10.0) / 10.0)
                        .durationMinutes((int) (dist * 6))
                        .build();
                sessionRepository.save(session);
                program.getSessions().add(session);
            }
        }
    }

    private String getFallbackPlan() {
        return "[]";
    }

    private int toInt(Object val, int def) {
        if (val == null) return def;
        if (val instanceof Number n) return n.intValue();
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return def; }
    }

    private double toDouble(Object val) {
        if (val == null) return 0;
        if (val instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return 0; }
    }
}
