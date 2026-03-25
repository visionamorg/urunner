package com.runhub.rankings.service;

import com.runhub.communities.repository.CommunityMemberRepository;
import com.runhub.rankings.dto.RankingDto;
import com.runhub.running.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final ActivityRepository activityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    public List<RankingDto> getGlobalRankings(String type) {
        List<Object[]> rows;
        LocalDate now = LocalDate.now();

        rows = switch (type) {
            case "weekly" -> activityRepository.findRankingsByDateRange(now.minusDays(7), now);
            case "monthly" -> activityRepository.findRankingsByDateRange(now.minusDays(30), now);
            default -> activityRepository.findAllTimeRankings();
        };

        return buildRankings(rows, "distance");
    }

    public List<RankingDto> getCommunityRankings(Long communityId) {
        return getCommunityRankings(communityId, "distance");
    }

    public List<RankingDto> getCommunityRankings(Long communityId, String metric) {
        List<Long> userIds = communityMemberRepository.findByCommunityId(communityId)
                .stream().map(m -> m.getUser().getId()).toList();

        if (userIds.isEmpty()) return List.of();

        List<Object[]> rows = switch (metric) {
            case "time" -> activityRepository.findRankingsByUserIdsSortByTime(userIds);
            case "elevation" -> activityRepository.findRankingsByUserIdsSortByElevation(userIds);
            default -> activityRepository.findRankingsByUserIds(userIds);
        };
        return buildRankings(rows, metric);
    }

    public List<RankingDto> getCommunityWeeklyChallenge(Long communityId) {
        List<Long> userIds = communityMemberRepository.findByCommunityId(communityId)
                .stream().map(m -> m.getUser().getId()).toList();
        if (userIds.isEmpty()) return List.of();

        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1); // Monday
        List<Object[]> rows = activityRepository.findRankingsByUserIdsAndDateRange(userIds, weekStart, now);
        return buildRankings(rows, "distance");
    }

    private List<RankingDto> buildRankings(List<Object[]> rows, String metric) {
        List<RankingDto> rankings = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            var builder = RankingDto.builder()
                    .rank(i + 1)
                    .userId(((Number) row[0]).longValue())
                    .username((String) row[1])
                    .profileImageUrl(row[4] != null ? (String) row[4] : null)
                    .totalRuns(row[6] != null ? ((Number) row[6]).longValue() : 0L);

            double val = row[5] != null ? ((Number) row[5]).doubleValue() : 0.0;
            switch (metric) {
                case "time" -> builder.totalDurationMinutes((long) val).totalDistanceKm(0.0);
                case "elevation" -> builder.totalElevationMeters((long) val).totalDistanceKm(0.0);
                default -> builder.totalDistanceKm(val);
            }
            rankings.add(builder.build());
        }
        return rankings;
    }
}
