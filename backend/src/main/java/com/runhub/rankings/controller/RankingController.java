package com.runhub.rankings.controller;

import com.runhub.rankings.dto.RankingDto;
import com.runhub.rankings.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/global")
    public ResponseEntity<List<RankingDto>> getGlobalRankings(
            @RequestParam(defaultValue = "alltime") String type) {
        return ResponseEntity.ok(rankingService.getGlobalRankings(type));
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<RankingDto>> getCommunityRankings(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "distance") String metric) {
        return ResponseEntity.ok(rankingService.getCommunityRankings(communityId, metric));
    }

    @GetMapping("/community/{communityId}/weekly")
    public ResponseEntity<List<RankingDto>> getCommunityWeeklyChallenge(@PathVariable Long communityId) {
        return ResponseEntity.ok(rankingService.getCommunityWeeklyChallenge(communityId));
    }
}
