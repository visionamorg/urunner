package com.runhub.badges.controller;

import com.runhub.badges.dto.BadgeDto;
import com.runhub.badges.dto.CreateBadgeRequest;
import com.runhub.badges.dto.UserBadgeDto;
import com.runhub.badges.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping
    public ResponseEntity<List<BadgeDto>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BadgeDto> createBadge(@RequestBody CreateBadgeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(badgeService.createBadge(request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<UserBadgeDto>> getMyBadges(Principal principal) {
        return ResponseEntity.ok(badgeService.getMyBadges(principal.getName()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBadgeDto>> getUserBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(badgeService.getUserBadges(userId));
    }
}
