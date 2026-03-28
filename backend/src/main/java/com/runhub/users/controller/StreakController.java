package com.runhub.users.controller;

import com.runhub.users.service.StreakFreezeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/streak")
@RequiredArgsConstructor
public class StreakController {

    private final StreakFreezeService streakService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStreak(Authentication auth) {
        return ResponseEntity.ok(streakService.getStreak(auth.getName()));
    }

    @PostMapping("/freeze")
    public ResponseEntity<Map<String, Object>> activateFreeze(Authentication auth) {
        return ResponseEntity.ok(streakService.activateFreeze(auth.getName()));
    }
}
