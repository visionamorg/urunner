package com.runhub.communities.controller;

import com.runhub.communities.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communities/{communityId}/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getChallenges(@PathVariable Long communityId) {
        return ResponseEntity.ok(challengeService.getChallenges(communityId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createChallenge(
            @PathVariable Long communityId,
            @RequestBody Map<String, Object> request,
            Authentication auth) {
        return ResponseEntity.ok(challengeService.createChallenge(communityId, request, auth.getName()));
    }

    @PostMapping("/{challengeId}/join")
    public ResponseEntity<Map<String, Object>> joinChallenge(
            @PathVariable Long communityId,
            @PathVariable Long challengeId,
            Authentication auth) {
        return ResponseEntity.ok(challengeService.joinChallenge(challengeId, auth.getName()));
    }

    @PostMapping("/{challengeId}/refresh")
    public ResponseEntity<Map<String, Object>> refreshProgress(
            @PathVariable Long communityId,
            @PathVariable Long challengeId) {
        return ResponseEntity.ok(challengeService.refreshProgress(challengeId));
    }

    @GetMapping("/{challengeId}/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard(
            @PathVariable Long communityId,
            @PathVariable Long challengeId) {
        return ResponseEntity.ok(challengeService.getLeaderboard(challengeId));
    }
}
