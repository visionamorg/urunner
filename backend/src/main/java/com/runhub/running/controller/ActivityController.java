package com.runhub.running.controller;

import com.runhub.running.dto.ActivityDto;
import com.runhub.running.dto.ActivityStatsDto;
import com.runhub.running.dto.CreateActivityRequest;
import com.runhub.running.dto.StreakDto;
import com.runhub.running.service.ActivityService;
import com.runhub.running.service.StreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final StreakService streakService;

    @GetMapping
    public ResponseEntity<List<ActivityDto>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDto> getActivityById(@PathVariable Long id, Principal principal) {
        return activityService.getActivityById(id, principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    public ResponseEntity<List<ActivityDto>> getMyActivities(Principal principal) {
        return ResponseEntity.ok(activityService.getUserActivities(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<ActivityDto> createActivity(Principal principal, @RequestBody CreateActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityService.createActivity(principal.getName(), request));
    }

    @GetMapping("/stats")
    public ResponseEntity<ActivityStatsDto> getMyStats(Principal principal) {
        return ResponseEntity.ok(activityService.getUserStats(principal.getName()));
    }

    @GetMapping("/streak")
    public ResponseEntity<StreakDto> getMyStreak(Principal principal) {
        return ResponseEntity.ok(streakService.getStreak(principal.getName()));
    }

    @PutMapping("/{id}/nutrition")
    public ResponseEntity<ActivityDto> updateNutrition(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> nutrition,
            Principal principal) {
        return ResponseEntity.ok(activityService.updateNutrition(id, nutrition, principal.getName()));
    }
}
