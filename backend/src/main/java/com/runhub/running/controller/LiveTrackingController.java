package com.runhub.running.controller;

import com.runhub.running.model.LiveTrackingSession;
import com.runhub.running.repository.LiveTrackingRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/live-tracking")
@RequiredArgsConstructor
public class LiveTrackingController {

    private final LiveTrackingRepository trackingRepository;
    private final UserService userService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startTracking(Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());

        // End any existing session
        trackingRepository.findByUserIdAndActiveTrue(user.getId())
                .ifPresent(s -> { s.setActive(false); trackingRepository.save(s); });

        String token = UUID.randomUUID().toString().replace("-", "");
        LiveTrackingSession session = LiveTrackingSession.builder()
                .user(user)
                .token(token)
                .build();
        trackingRepository.save(session);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("shareUrl", "/track/" + token);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateLocation(
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        trackingRepository.findByUserIdAndActiveTrue(user.getId()).ifPresent(s -> {
            s.setLatitude(Double.parseDouble(body.get("latitude").toString()));
            s.setLongitude(Double.parseDouble(body.get("longitude").toString()));
            s.setLastUpdate(LocalDateTime.now());
            trackingRepository.save(s);
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopTracking(Authentication auth) {
        User user = userService.getUserEntityByEmail(auth.getName());
        trackingRepository.findByUserIdAndActiveTrue(user.getId())
                .ifPresent(s -> { s.setActive(false); trackingRepository.save(s); });
        return ResponseEntity.ok().build();
    }

    // Returns all currently active live sessions (for LIVE NOW section in feed)
    @GetMapping("/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveSessions() {
        List<Map<String, Object>> result = trackingRepository.findAllByActiveTrue().stream()
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("token", s.getToken());
                    m.put("username", s.getUser().getDisplayUsername());
                    m.put("profileImageUrl", s.getUser().getProfileImageUrl());
                    m.put("latitude", s.getLatitude());
                    m.put("longitude", s.getLongitude());
                    m.put("lastUpdate", s.getLastUpdate() != null ? s.getLastUpdate().toString() : null);
                    m.put("garminLiveTrackUrl", s.getGarminLiveTrackUrl());
                    m.put("shareUrl", "/track/" + s.getToken());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // Public endpoint: no auth required - Safety Buddy views this
    @GetMapping("/view/{token}")
    public ResponseEntity<Map<String, Object>> viewTracking(@PathVariable String token) {
        return trackingRepository.findByToken(token)
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("active", s.getActive());
                    m.put("latitude", s.getLatitude());
                    m.put("longitude", s.getLongitude());
                    m.put("lastUpdate", s.getLastUpdate() != null ? s.getLastUpdate().toString() : null);
                    m.put("username", s.getUser().getDisplayUsername());

                    // Check for inactivity alert (5+ minutes)
                    boolean noActivity = s.getActive() && s.getLastUpdate() != null
                            && s.getLastUpdate().isBefore(LocalDateTime.now().minusMinutes(5));
                    m.put("inactivityAlert", noActivity);

                    return ResponseEntity.ok(m);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
