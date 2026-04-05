package com.runhub.rankings.controller;

import com.runhub.rankings.dto.RankingDto;
import com.runhub.rankings.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
@Tag(name = "Rankings", description = "Global and community leaderboards based on distance, pace, and weekly challenges")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/global")
    @Operation(summary = "Global leaderboard", description = "Returns the platform-wide rankings. Use ?type=alltime|weekly|monthly")
    public ResponseEntity<List<RankingDto>> getGlobalRankings(
            @RequestParam(defaultValue = "alltime") String type) {
        return ResponseEntity.ok(rankingService.getGlobalRankings(type));
    }

    @GetMapping("/community/{communityId}")
    @Operation(summary = "Community leaderboard", description = "Returns rankings within a specific community. Use ?metric=distance|pace|elevation")
    public ResponseEntity<List<RankingDto>> getCommunityRankings(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "distance") String metric) {
        return ResponseEntity.ok(rankingService.getCommunityRankings(communityId, metric));
    }

    @GetMapping("/community/{communityId}/weekly")
    @Operation(summary = "Community weekly challenge", description = "Returns the weekly mileage challenge standings for a community")
    public ResponseEntity<List<RankingDto>> getCommunityWeeklyChallenge(@PathVariable Long communityId) {
        return ResponseEntity.ok(rankingService.getCommunityWeeklyChallenge(communityId));
    }
}
