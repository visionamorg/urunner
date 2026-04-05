package com.runhub.running.controller;

import com.runhub.running.dto.ActivityDto;
import com.runhub.running.dto.ActivityStatsDto;
import com.runhub.running.dto.CreateActivityRequest;
import com.runhub.running.dto.StreakDto;
import com.runhub.running.service.ActivityService;
import com.runhub.running.service.StreakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Tag(name = "Activities", description = "Log and retrieve running activities, stats, and streaks")
public class ActivityController {

    private final ActivityService activityService;
    private final StreakService streakService;

    @GetMapping
    @Operation(summary = "Get all activities", description = "Returns all activities across all users (admin use)")
    public ResponseEntity<List<ActivityDto>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get activity by ID", description = "Returns a single activity. Only the owner can access private activities.")
    public ResponseEntity<ActivityDto> getActivityById(@PathVariable Long id, Principal principal) {
        return activityService.getActivityById(id, principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    @Operation(summary = "Get my activities", description = "Returns all activities for the authenticated user")
    public ResponseEntity<List<ActivityDto>> getMyActivities(Principal principal) {
        return ResponseEntity.ok(activityService.getUserActivities(principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Log a new activity", description = "Creates a manual running activity for the authenticated user")
    public ResponseEntity<ActivityDto> createActivity(Principal principal, @RequestBody CreateActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityService.createActivity(principal.getName(), request));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get my stats", description = "Returns aggregate statistics (total distance, time, pace) for the authenticated user")
    public ResponseEntity<ActivityStatsDto> getMyStats(Principal principal) {
        return ResponseEntity.ok(activityService.getUserStats(principal.getName()));
    }

    @GetMapping("/streak")
    @Operation(summary = "Get my streak", description = "Returns the current and longest activity streak for the authenticated user")
    public ResponseEntity<StreakDto> getMyStreak(Principal principal) {
        return ResponseEntity.ok(streakService.getStreak(principal.getName()));
    }

    @PutMapping("/{id}/nutrition")
    @Operation(summary = "Update nutrition for activity", description = "Attaches nutrition data (calories, hydration, etc.) to a specific activity")
    public ResponseEntity<ActivityDto> updateNutrition(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> nutrition,
            Principal principal) {
        return ResponseEntity.ok(activityService.updateNutrition(id, nutrition, principal.getName()));
    }
}
