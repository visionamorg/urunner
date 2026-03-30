package com.runhub.running.controller;

import com.runhub.running.dto.HealthMetricDto;
import com.runhub.running.dto.SyncResultDto;
import com.runhub.running.model.HealthMetric;
import com.runhub.running.repository.HealthMetricRepository;
import com.runhub.running.service.GarminHealthSyncService;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/garmin/health")
@RequiredArgsConstructor
public class HealthMetricController {

    private final GarminHealthSyncService garminHealthSyncService;
    private final HealthMetricRepository healthMetricRepository;
    private final UserService userService;

    /**
     * Trigger a health data sync from Garmin.
     * POST /api/garmin/health/sync
     */
    @PostMapping("/sync")
    public ResponseEntity<SyncResultDto> syncHealth(Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        SyncResultDto result = garminHealthSyncService.syncHealthData(user);
        return ResponseEntity.ok(result);
    }

    /**
     * Get last 30 days of health metrics for the authenticated user.
     * GET /api/garmin/health/metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<List<HealthMetricDto>> getMetrics(Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        List<HealthMetric> metrics = healthMetricRepository.findTop30ByUserIdOrderByDateDesc(user.getId());
        List<HealthMetricDto> dtos = metrics.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private HealthMetricDto toDto(HealthMetric m) {
        return HealthMetricDto.builder()
                .id(m.getId())
                .date(m.getDate())
                .restingHeartRate(m.getRestingHeartRate())
                .sleepScore(m.getSleepScore())
                .vo2Max(m.getVo2Max())
                .fitnessAge(m.getFitnessAge())
                .hrvStatus(m.getHrvStatus())
                .bodyBatteryMax(m.getBodyBatteryMax())
                .stressLevel(m.getStressLevel())
                .build();
    }
}
