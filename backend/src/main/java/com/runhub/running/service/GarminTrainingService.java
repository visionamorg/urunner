package com.runhub.running.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.runhub.auth.service.GarminOAuthService;
import com.runhub.programs.model.ProgramSession;
import com.runhub.programs.model.UserProgramProgress;
import com.runhub.running.model.GarminWorkout;
import com.runhub.running.model.WorkoutStep;
import com.runhub.programs.repository.ProgramSessionRepository;
import com.runhub.programs.repository.UserProgramProgressRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pushes training sessions and full programs to Garmin Training API.
 *
 * Garmin Training API endpoints used:
 *   POST https://apis.garmin.com/training-api/rest/workout       — create workout
 *   POST https://apis.garmin.com/training-api/rest/schedule      — schedule workout to calendar date
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GarminTrainingService {

    private static final String GARMIN_WORKOUT_URL = "https://apis.garmin.com/training-api/rest/workout";
    private static final String GARMIN_SCHEDULE_URL = "https://apis.garmin.com/training-api/rest/schedule";
    private static final int BATCH_SIZE = 20;
    private static final long BATCH_SLEEP_MS = 100;

    private final GarminOAuthService garminOAuthService;
    private final ProgramSessionRepository sessionRepository;
    private final UserProgramProgressRepository progressRepository;
    private final ObjectMapper objectMapper;

    // ── G004: Push a single workout ─────────────────────────────────────────

    public Map<String, Object> pushWorkoutToGarmin(User user, Long sessionId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            ProgramSession session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

            String workoutJson = buildWorkoutJson(session);
            String response = executePost(user, GARMIN_WORKOUT_URL, workoutJson);

            JsonNode responseNode = objectMapper.readTree(response);
            String garminWorkoutId = responseNode.has("workoutId")
                    ? responseNode.get("workoutId").asText()
                    : (responseNode.has("id") ? responseNode.get("id").asText() : null);

            result.put("success", true);
            result.put("garminWorkoutId", garminWorkoutId);
            result.put("sessionId", sessionId);
            result.put("title", session.getTitle());
        } catch (Exception e) {
            log.error("Failed to push workout {} to Garmin for user {}", sessionId, user.getId(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("sessionId", sessionId);
        }
        return result;
    }

    // ── G005: Sync entire program or next 7 days ────────────────────────────

    public Map<String, Object> syncProgramToGarmin(User user, Long progressId, boolean nextWeekOnly) {
        Map<String, Object> summary = new LinkedHashMap<>();
        int pushed = 0, failed = 0, scheduled = 0;

        try {
            UserProgramProgress progress = progressRepository.findById(progressId)
                    .orElseThrow(() -> new IllegalArgumentException("Progress not found: " + progressId));

            // Verify the progress belongs to this user
            if (!progress.getUser().getId().equals(user.getId())) {
                throw new SecurityException("Access denied to program progress " + progressId);
            }

            List<ProgramSession> sessions = sessionRepository
                    .findByProgramIdOrderByWeekNumberAscDayNumberAsc(progress.getProgram().getId());

            // Calculate start date from when the user started the program
            LocalDate programStartDate = progress.getStartedAt().toLocalDate();
            LocalDate cutoffDate = nextWeekOnly ? LocalDate.now().plusDays(7) : null;

            int batchCount = 0;
            for (ProgramSession session : sessions) {
                // Calculate the scheduled date for this session
                // weekNumber and dayNumber are 1-based
                int daysOffset = (session.getWeekNumber() - 1) * 7 + (session.getDayNumber() - 1);
                LocalDate scheduledDate = programStartDate.plusDays(daysOffset);

                // Skip past sessions when syncing next week only
                if (nextWeekOnly && scheduledDate.isBefore(LocalDate.now())) continue;
                if (cutoffDate != null && scheduledDate.isAfter(cutoffDate)) continue;

                try {
                    // Push workout definition
                    String workoutJson = buildWorkoutJson(session);
                    String workoutResponse = executePost(user, GARMIN_WORKOUT_URL, workoutJson);
                    JsonNode wNode = objectMapper.readTree(workoutResponse);
                    String garminWorkoutId = wNode.has("workoutId")
                            ? wNode.get("workoutId").asText()
                            : (wNode.has("id") ? wNode.get("id").asText() : null);
                    pushed++;

                    // Schedule to calendar if we have an ID
                    if (garminWorkoutId != null && !garminWorkoutId.isBlank()) {
                        String scheduleJson = objectMapper.writeValueAsString(Map.of(
                                "workoutId", garminWorkoutId,
                                "date", scheduledDate.toString()
                        ));
                        executePost(user, GARMIN_SCHEDULE_URL, scheduleJson);
                        scheduled++;
                    }

                    // Batch rate limiting
                    batchCount++;
                    if (batchCount % BATCH_SIZE == 0) {
                        Thread.sleep(BATCH_SLEEP_MS);
                    }
                } catch (Exception e) {
                    log.warn("Failed to push session {} for user {}: {}", session.getId(), user.getId(), e.getMessage());
                    failed++;
                }
            }

            summary.put("success", true);
            summary.put("pushed", pushed);
            summary.put("scheduled", scheduled);
            summary.put("failed", failed);
            summary.put("programName", progress.getProgram().getName());

        } catch (Exception e) {
            log.error("Failed to sync program progress {} for user {}", progressId, user.getId(), e);
            summary.put("success", false);
            summary.put("error", e.getMessage());
            summary.put("pushed", pushed);
            summary.put("failed", failed);
        }

        return summary;
    }

    // ── G006: Push a structured GarminWorkout ────────────────────────────────

    public Map<String, Object> pushStructuredWorkout(User user, GarminWorkout workout, String scheduledDate) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            String workoutJson = buildStructuredWorkoutJson(workout);
            String response = executePost(user, GARMIN_WORKOUT_URL, workoutJson);

            JsonNode responseNode = objectMapper.readTree(response);
            String garminWorkoutId = responseNode.has("workoutId")
                    ? responseNode.get("workoutId").asText()
                    : (responseNode.has("id") ? responseNode.get("id").asText() : null);

            // Schedule to calendar date if provided
            if (garminWorkoutId != null && !garminWorkoutId.isBlank() && scheduledDate != null && !scheduledDate.isBlank()) {
                String scheduleJson = objectMapper.writeValueAsString(Map.of(
                        "workoutId", garminWorkoutId,
                        "date", scheduledDate
                ));
                executePost(user, GARMIN_SCHEDULE_URL, scheduleJson);
            }

            result.put("success", true);
            result.put("garminWorkoutId", garminWorkoutId);
            result.put("workoutId", workout.getId());
            result.put("title", workout.getTitle());
        } catch (Exception e) {
            log.error("Failed to push structured workout {} for user {}", workout.getId(), user.getId(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("workoutId", workout.getId());
        }
        return result;
    }

    private String buildStructuredWorkoutJson(GarminWorkout workout) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("workoutName", workout.getTitle());
        root.put("description", workout.getDescription() != null ? workout.getDescription() : "");
        root.put("sport", workout.getSport() != null ? workout.getSport() : "RUNNING");

        ArrayNode stepsNode = objectMapper.createArrayNode();
        for (WorkoutStep step : workout.getSteps()) {
            stepsNode.add(serializeStep(step));
        }
        root.set("steps", stepsNode);
        return objectMapper.writeValueAsString(root);
    }

    private ObjectNode serializeStep(WorkoutStep step) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("type", step.getStepType() != null ? step.getStepType() : "INTERVAL");
        node.put("notes", step.getNotes() != null ? step.getNotes() : "");

        // Duration
        ObjectNode duration = objectMapper.createObjectNode();
        String durationUnit = step.getDurationUnit();
        if (durationUnit == null || durationUnit.equals("OPEN") || durationUnit.equals("LAP_BUTTON")) {
            duration.put("type", durationUnit != null ? durationUnit : "OPEN");
        } else {
            duration.put("type", durationUnit);
            duration.put("value", step.getDurationValue() != null ? step.getDurationValue() : 0L);
        }
        node.set("duration", duration);

        // Target
        ObjectNode target = objectMapper.createObjectNode();
        String targetType = step.getTargetType() != null ? step.getTargetType() : "NO_TARGET";
        target.put("type", targetType);
        if (step.getTargetLow() != null && !"NO_TARGET".equals(targetType)) {
            if ("PACE".equals(targetType)) {
                // Frontend stores sec/km; Garmin API expects sec/meter
                target.put("targetLow", step.getTargetLow() / 1000.0);
                target.put("targetHigh", step.getTargetHigh() != null ? step.getTargetHigh() / 1000.0 : step.getTargetLow() / 1000.0);
            } else {
                target.put("targetLow", step.getTargetLow());
                target.put("targetHigh", step.getTargetHigh() != null ? step.getTargetHigh() : step.getTargetLow());
            }
        }
        node.set("target", target);

        // REPEAT step with nested children
        if ("REPEAT".equals(step.getStepType())) {
            node.put("repeatCount", step.getRepeatCount() != null ? step.getRepeatCount() : 1);
            if (step.getChildren() != null && !step.getChildren().isEmpty()) {
                ArrayNode childNodes = objectMapper.createArrayNode();
                for (WorkoutStep child : step.getChildren()) {
                    childNodes.add(serializeStep(child));
                }
                node.set("steps", childNodes);
            }
        }

        return node;
    }

    // ── Helper: build Garmin workout JSON from ProgramSession ───────────────

    private String buildWorkoutJson(ProgramSession session) throws Exception {
        ObjectNode workout = objectMapper.createObjectNode();
        workout.put("workoutName", session.getTitle());
        workout.put("description", session.getDescription() != null ? session.getDescription() : "");
        workout.put("sport", "RUNNING");

        ArrayNode steps = objectMapper.createArrayNode();

        // Warmup step
        ObjectNode warmup = buildStep("WARMUP", "OPEN", null, null,
                "Warm up at easy effort", 5 * 60 * 1000); // 5 min in ms
        steps.add(warmup);

        // Main interval step based on session distance/duration
        String mainTarget = null;
        Integer mainDuration = null;
        String mainDurationUnit = null;

        if (session.getDistanceKm() != null && session.getDistanceKm() > 0) {
            mainTarget = "NO_TARGET";
            mainDuration = (int)(session.getDistanceKm() * 1000); // meters
            mainDurationUnit = "DISTANCE";
        } else if (session.getDurationMinutes() != null && session.getDurationMinutes() > 0) {
            mainTarget = "NO_TARGET";
            mainDuration = session.getDurationMinutes() * 60 * 1000; // ms
            mainDurationUnit = "TIME";
        }

        ObjectNode main = buildStep("INTERVAL", mainDurationUnit, mainDuration, mainTarget,
                session.getDescription() != null ? session.getDescription() : session.getTitle(), null);
        steps.add(main);

        // Cooldown step
        ObjectNode cooldown = buildStep("COOLDOWN", "OPEN", null, null,
                "Cool down at easy effort", 5 * 60 * 1000);
        steps.add(cooldown);

        workout.set("steps", steps);
        return objectMapper.writeValueAsString(workout);
    }

    private ObjectNode buildStep(String type, String durationUnit, Integer durationValue,
                                  String targetType, String notes, Integer openDurationMs) {
        ObjectNode step = objectMapper.createObjectNode();
        step.put("type", type);
        step.put("notes", notes != null ? notes : "");

        ObjectNode duration = objectMapper.createObjectNode();
        if (durationUnit != null && durationValue != null) {
            duration.put("type", durationUnit);
            duration.put("value", durationValue);
        } else {
            duration.put("type", "TIME");
            duration.put("value", openDurationMs != null ? openDurationMs : 5 * 60 * 1000);
        }
        step.set("duration", duration);

        ObjectNode target = objectMapper.createObjectNode();
        target.put("type", targetType != null ? targetType : "NO_TARGET");
        step.set("target", target);

        return step;
    }

    // ── Helper: execute signed OAuth1 POST ─────────────────────────────────

    private String executePost(User user, String url, String body) throws Exception {
        OAuth10aService service = garminOAuthService.getService();
        OAuthRequest request = new OAuthRequest(Verb.POST, url);
        request.addHeader("Content-Type", "application/json");
        request.setPayload(body);
        service.signRequest(garminOAuthService.buildAccessToken(user), request);

        try (Response response = service.execute(request)) {
            String responseBody = response.getBody();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Garmin API error " + response.getCode() + ": " + responseBody);
            }
            return responseBody;
        }
    }
}
