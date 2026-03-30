package com.runhub.running.controller;

import com.runhub.running.service.GarminTrainingService;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/garmin/training")
@RequiredArgsConstructor
public class GarminTrainingController {

    private final GarminTrainingService garminTrainingService;
    private final UserService userService;

    /**
     * G004 — Push a single program session as a Garmin workout.
     * POST /api/garmin/training/workout/{sessionId}
     */
    @PostMapping("/workout/{sessionId}")
    public ResponseEntity<Map<String, Object>> pushWorkout(
            @PathVariable Long sessionId,
            Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        Map<String, Object> result = garminTrainingService.pushWorkoutToGarmin(user, sessionId);
        return ResponseEntity.ok(result);
    }

    /**
     * G005 — Sync all sessions of a user's program progress to Garmin calendar.
     * POST /api/garmin/training/program/{progressId}/sync
     */
    @PostMapping("/program/{progressId}/sync")
    public ResponseEntity<Map<String, Object>> syncProgram(
            @PathVariable Long progressId,
            Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        Map<String, Object> result = garminTrainingService.syncProgramToGarmin(user, progressId, false);
        return ResponseEntity.ok(result);
    }

    /**
     * G005 — Sync only the next 7 days of a program to Garmin calendar.
     * POST /api/garmin/training/program/{progressId}/sync-week
     */
    @PostMapping("/program/{progressId}/sync-week")
    public ResponseEntity<Map<String, Object>> syncProgramNextWeek(
            @PathVariable Long progressId,
            Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        Map<String, Object> result = garminTrainingService.syncProgramToGarmin(user, progressId, true);
        return ResponseEntity.ok(result);
    }
}
