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

        return buildRankings(rows);
    }

    public List<RankingDto> getCommunityRankings(Long communityId) {
        List<Long> userIds = communityMemberRepository.findByCommunityId(communityId)
                .stream().map(m -> m.getUser().getId()).toList();

        if (userIds.isEmpty()) return List.of();

        List<Object[]> rows = activityRepository.findRankingsByUserIds(userIds);
        return buildRankings(rows);
    }

    private List<RankingDto> buildRankings(List<Object[]> rows) {
        List<RankingDto> rankings = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            rankings.add(RankingDto.builder()
                    .rank(i + 1)
                    .userId(((Number) row[0]).longValue())
                    .username((String) row[1])
                    .profileImageUrl(row[4] != null ? (String) row[4] : null)
                    .totalDistanceKm(row[5] != null ? ((Number) row[5]).doubleValue() : 0.0)
                    .totalRuns(row[6] != null ? ((Number) row[6]).longValue() : 0L)
                    .build());
        }
        return rankings;
    }
}
