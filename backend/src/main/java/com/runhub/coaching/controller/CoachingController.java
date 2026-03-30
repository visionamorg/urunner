package com.runhub.coaching.controller;

import com.runhub.coaching.dto.*;
import com.runhub.coaching.repository.CoachingConnectionRepository;
import com.runhub.coaching.service.CoachingCommentService;
import com.runhub.coaching.service.CoachingService;
import com.runhub.running.model.HealthMetric;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.running.repository.HealthMetricRepository;
import com.runhub.running.service.PerformanceAnalyticsService;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coaching")
@RequiredArgsConstructor
public class CoachingController {

    private final CoachingService coachingService;
    private final CoachingCommentService commentService;
    private final CoachingConnectionRepository connectionRepository;
    private final ActivityRepository activityRepository;
    private final HealthMetricRepository healthMetricRepository;
    private final PerformanceAnalyticsService analyticsService;
    private final UserService userService;

    // ── GC-001: Linkage endpoints ────────────────────────────────────────────

    @PostMapping("/invite")
    public ResponseEntity<CoachingConnectionDto> inviteAthlete(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        User coach = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(coachingService.inviteAthlete(
                coach,
                body.get("usernameOrEmail"),
                body.get("accessLevel")));
    }

    @PostMapping("/accept/{token}")
    public ResponseEntity<CoachingConnectionDto> acceptInvite(
            @PathVariable String token,
            Authentication auth) {
        User athlete = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(coachingService.acceptInvite(athlete, token));
    }

    @DeleteMapping("/{id}/revoke")
    public ResponseEntity<Void> revokeConnection(
            @PathVariable Long id,
            Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        coachingService.revokeConnection(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-athletes")
    public ResponseEntity<List<CoachingConnectionDto>> getMyAthletes(Authentication auth) {
        User coach = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(coachingService.getMyAthletes(coach));
    }

    @GetMapping("/my-coaches")
    public ResponseEntity<List<CoachingConnectionDto>> getMyCoaches(Authentication auth) {
        User athlete = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(coachingService.getMyCoaches(athlete));
    }

    @GetMapping("/pending-invites")
    public ResponseEntity<List<CoachingConnectionDto>> getPendingInvites(Authentication auth) {
        User athlete = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(coachingService.getPendingInvites(athlete));
    }

    // ── GC-002: Team feed ────────────────────────────────────────────────────

    @GetMapping("/team/feed")
    public ResponseEntity<List<Map<String, Object>>> getTeamFeed(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication auth) {

        User coach = userService.getUserEntityByEmail(auth.getName());
        List<Long> athleteIds = connectionRepository.findByCoachIdAndStatus(coach.getId(), "ACTIVE")
                .stream().map(c -> c.getAthlete().getId()).collect(Collectors.toList());

        List<Map<String, Object>> feed = new ArrayList<>();
        for (Long athleteId : athleteIds) {
            List<RunningActivity> activities = activityRepository.findByUserIdOrderByActivityDateDesc(athleteId);
            for (RunningActivity a : activities) {
                if (from != null && a.getActivityDate().isBefore(from)) continue;
                if (to != null && a.getActivityDate().isAfter(to)) continue;

                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("activityId", a.getId());
                entry.put("athleteId", a.getUser().getId());
                entry.put("athleteUsername", a.getUser().getDisplayUsername());
                entry.put("athleteProfileImageUrl", a.getUser().getProfileImageUrl());
                entry.put("title", a.getTitle());
                entry.put("distanceKm", a.getDistanceKm());
                entry.put("durationMinutes", a.getDurationMinutes());
                entry.put("paceMinPerKm", a.getPaceMinPerKm());
                entry.put("avgHeartRate", a.getAvgHeartRate());
                entry.put("activityDate", a.getActivityDate());
                entry.put("source", a.getSource());
                feed.add(entry);
            }
        }

        // Sort by date desc, limit 50
        feed.sort((a, b) -> {
            LocalDate da = (LocalDate) a.get("activityDate");
            LocalDate db = (LocalDate) b.get("activityDate");
            return db.compareTo(da);
        });

        return ResponseEntity.ok(feed.stream().limit(50).collect(Collectors.toList()));
    }

    // ── GC-003: Team readiness ───────────────────────────────────────────────

    @GetMapping("/team/readiness")
    public ResponseEntity<List<Map<String, Object>>> getTeamReadiness(Authentication auth) {
        User coach = userService.getUserEntityByEmail(auth.getName());
        List<Long> athleteIds = connectionRepository.findByCoachIdAndStatus(coach.getId(), "ACTIVE")
                .stream().map(c -> c.getAthlete().getId()).collect(Collectors.toList());

        List<Map<String, Object>> readiness = new ArrayList<>();
        for (Long athleteId : athleteIds) {
            // Get athlete info from first connection
            connectionRepository.findByCoachIdAndAthleteId(coach.getId(), athleteId).ifPresent(conn -> {
                User athlete = conn.getAthlete();
                Optional<HealthMetric> metricOpt = healthMetricRepository.findTopByUserIdOrderByDateDesc(athleteId);

                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("athleteId", athleteId);
                entry.put("username", athlete.getDisplayUsername());
                entry.put("profileImageUrl", athlete.getProfileImageUrl());

                if (metricOpt.isPresent()) {
                    HealthMetric m = metricOpt.get();
                    entry.put("date", m.getDate());
                    entry.put("restingHeartRate", m.getRestingHeartRate());
                    entry.put("sleepScore", m.getSleepScore());
                    entry.put("vo2Max", m.getVo2Max());
                    entry.put("bodyBatteryMax", m.getBodyBatteryMax());
                    entry.put("hrvStatus", m.getHrvStatus());
                    entry.put("stressLevel", m.getStressLevel());
                    entry.put("risk", computeRisk(m));
                } else {
                    entry.put("date", null);
                    entry.put("restingHeartRate", null);
                    entry.put("sleepScore", null);
                    entry.put("vo2Max", null);
                    entry.put("bodyBatteryMax", null);
                    entry.put("hrvStatus", null);
                    entry.put("stressLevel", null);
                    entry.put("risk", "UNKNOWN");
                }
                readiness.add(entry);
            });
        }
        return ResponseEntity.ok(readiness);
    }

    // ── GC-005: Team analytics ───────────────────────────────────────────────

    @GetMapping("/team/analytics")
    public ResponseEntity<List<Map<String, Object>>> getTeamAnalytics(Authentication auth) {
        User coach = userService.getUserEntityByEmail(auth.getName());
        List<Long> athleteIds = connectionRepository.findByCoachIdAndStatus(coach.getId(), "ACTIVE")
                .stream().map(c -> c.getAthlete().getId()).collect(Collectors.toList());

        List<Map<String, Object>> teamMetrics = analyticsService.getTeamLoadMetrics(athleteIds);

        // Enrich with usernames
        for (Map<String, Object> metric : teamMetrics) {
            Long athleteId = (Long) metric.get("userId");
            connectionRepository.findByCoachIdAndAthleteId(coach.getId(), athleteId).ifPresent(conn -> {
                metric.put("username", conn.getAthlete().getDisplayUsername());
                metric.put("profileImageUrl", conn.getAthlete().getProfileImageUrl());
            });
        }

        return ResponseEntity.ok(teamMetrics);
    }

    // ── GC-006: Coaching comments ────────────────────────────────────────────

    @PostMapping("/comments")
    public ResponseEntity<CoachingCommentDto> addComment(
            @RequestBody CreateCommentRequest request,
            Authentication auth) {
        User coach = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(commentService.addComment(coach, request));
    }

    @GetMapping("/activities/{id}/comments")
    public ResponseEntity<List<CoachingCommentDto>> getActivityComments(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsForActivity(id));
    }

    @PutMapping("/comments/{id}/pin")
    public ResponseEntity<CoachingCommentDto> pinComment(
            @PathVariable Long id,
            Authentication auth) {
        User coach = userService.getUserEntityByEmail(auth.getName());
        return ResponseEntity.ok(commentService.pinComment(coach, id));
    }

    // ── Risk calculation ─────────────────────────────────────────────────────

    private String computeRisk(HealthMetric m) {
        Integer sleep = m.getSleepScore();
        Integer battery = m.getBodyBatteryMax();
        Integer rhr = m.getRestingHeartRate();

        if (sleep != null && sleep >= 75
                && battery != null && battery >= 70
                && (rhr == null || rhr <= 65)) {
            return "GREEN";
        }
        if ((sleep != null && sleep >= 50) || (battery != null && battery >= 40)) {
            return "YELLOW";
        }
        return "RED";
    }
}
