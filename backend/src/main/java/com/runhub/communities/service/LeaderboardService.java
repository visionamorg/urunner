package com.runhub.communities.service;

import com.runhub.communities.dto.ActiveChallengeDto;
import com.runhub.communities.dto.LeaderboardEntryDto;
import com.runhub.communities.model.Challenge;
import com.runhub.communities.model.CommunityMember;
import com.runhub.communities.repository.ChallengeParticipantRepository;
import com.runhub.communities.repository.ChallengeRepository;
import com.runhub.communities.repository.CommunityMemberRepository;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final ActivityRepository activityRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;

    /**
     * Returns top 10 leaderboard entries for a community.
     * Members who opted out are excluded.
     * Period: WEEK (current week Mon-today), MONTH (last 30 days), ALL (all time)
     */
    public List<LeaderboardEntryDto> getLeaderboard(Long communityId, String metric, String period) {
        List<Long> optInUserIds = communityMemberRepository.findOptInUserIdsByCommunityId(communityId);
        if (optInUserIds.isEmpty()) return List.of();

        String normalizedMetric = metric != null ? metric.toUpperCase() : "DISTANCE";
        String normalizedPeriod = period != null ? period.toUpperCase() : "ALL";

        LocalDate now = LocalDate.now();
        List<Object[]> rows;

        if ("WEEK".equals(normalizedPeriod)) {
            LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1L);
            rows = switch (normalizedMetric) {
                case "TIME" -> activityRepository.findRankingsByUserIdsAndDateRangeSortByTime(optInUserIds, weekStart, now);
                case "ELEVATION" -> activityRepository.findRankingsByUserIdsAndDateRangeSortByElevation(optInUserIds, weekStart, now);
                default -> activityRepository.findRankingsByUserIdsAndDateRange(optInUserIds, weekStart, now);
            };
        } else if ("MONTH".equals(normalizedPeriod)) {
            LocalDate monthStart = now.minusDays(30);
            rows = switch (normalizedMetric) {
                case "TIME" -> activityRepository.findRankingsByUserIdsAndDateRangeSortByTime(optInUserIds, monthStart, now);
                case "ELEVATION" -> activityRepository.findRankingsByUserIdsAndDateRangeSortByElevation(optInUserIds, monthStart, now);
                default -> activityRepository.findRankingsByUserIdsAndDateRange(optInUserIds, monthStart, now);
            };
        } else {
            rows = switch (normalizedMetric) {
                case "TIME" -> activityRepository.findRankingsByUserIdsSortByTime(optInUserIds);
                case "ELEVATION" -> activityRepository.findRankingsByUserIdsSortByElevation(optInUserIds);
                default -> activityRepository.findRankingsByUserIds(optInUserIds);
            };
        }

        List<LeaderboardEntryDto> result = new ArrayList<>();
        int limit = Math.min(10, rows.size());
        for (int i = 0; i < limit; i++) {
            Object[] row = rows.get(i);
            double val = row[5] != null ? ((Number) row[5]).doubleValue() : 0.0;
            long runs = row[6] != null ? ((Number) row[6]).longValue() : 0L;

            LeaderboardEntryDto.LeaderboardEntryDtoBuilder builder = LeaderboardEntryDto.builder()
                    .rank(i + 1)
                    .userId(((Number) row[0]).longValue())
                    .username((String) row[1])
                    .profileImageUrl(row[4] != null ? (String) row[4] : null)
                    .totalRuns(runs)
                    .metric(normalizedMetric)
                    .value(val);

            switch (normalizedMetric) {
                case "TIME" -> builder.totalDurationMinutes((long) val).totalDistanceKm(0.0).totalElevationMeters(0L);
                case "ELEVATION" -> builder.totalElevationMeters((long) val).totalDistanceKm(0.0).totalDurationMinutes(0L);
                default -> builder.totalDistanceKm(val).totalDurationMinutes(0L).totalElevationMeters(0L);
            }

            result.add(builder.build());
        }
        return result;
    }

    /**
     * Returns active challenges with per-member progress bars.
     */
    public List<ActiveChallengeDto> getActiveChallenges(Long communityId) {
        List<Challenge> active = challengeRepository.findByCommunityIdAndStatus(communityId, "ACTIVE");

        return active.stream().map(c -> {
            double pct = c.getTargetValue() > 0
                    ? Math.min(100, (c.getCurrentValue() / c.getTargetValue()) * 100)
                    : 0;

            List<ActiveChallengeDto.MemberProgressDto> memberProgress =
                    challengeParticipantRepository.findByChallengeIdOrderByContributionDesc(c.getId())
                            .stream().map(cp -> {
                                double memberPct = c.getTargetValue() > 0
                                        ? Math.min(100, (cp.getContribution() / c.getTargetValue()) * 100)
                                        : 0;
                                return ActiveChallengeDto.MemberProgressDto.builder()
                                        .userId(cp.getUser().getId())
                                        .username(cp.getUser().getDisplayUsername())
                                        .profileImageUrl(cp.getUser().getProfileImageUrl())
                                        .contribution(cp.getContribution())
                                        .progressPercent(memberPct)
                                        .build();
                            }).toList();

            return ActiveChallengeDto.builder()
                    .id(c.getId())
                    .title(c.getTitle())
                    .description(c.getDescription())
                    .targetType(c.getTargetType())
                    .targetValue(c.getTargetValue())
                    .currentValue(c.getCurrentValue())
                    .startDate(c.getStartDate().toString())
                    .endDate(c.getEndDate().toString())
                    .status(c.getStatus())
                    .participantCount(c.getParticipantCount())
                    .progressPercent(pct)
                    .memberProgress(memberProgress)
                    .build();
        }).toList();
    }

    /**
     * Updates the community's preferred leaderboard metric. Admin only.
     */
    @Transactional
    public void updateLeaderboardMetric(Long communityId, String metric, User admin) {
        var community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        String normalizedMetric = metric != null ? metric.toUpperCase() : "DISTANCE";
        if (!List.of("DISTANCE", "TIME", "ELEVATION").contains(normalizedMetric)) {
            throw new BadRequestException("Invalid metric. Must be DISTANCE, TIME, or ELEVATION");
        }

        boolean isAdmin = communityMemberRepository.findByCommunityIdAndUserId(communityId, admin.getId())
                .map(m -> "ADMIN".equals(m.getRole()))
                .orElse(false);
        boolean isCreator = community.getCreator().getId().equals(admin.getId());
        if (!isAdmin && !isCreator) {
            throw new BadRequestException("Only community admins can change leaderboard metric");
        }

        community.setLeaderboardMetric(normalizedMetric);
        communityRepository.save(community);
    }

    /**
     * Toggles leaderboard opt-out for the current user.
     */
    @Transactional
    public boolean toggleOptOut(Long communityId, User user) {
        CommunityMember member = communityMemberRepository.findByCommunityIdAndUserId(communityId, user.getId())
                .orElseThrow(() -> new BadRequestException("You are not a member of this community"));

        boolean newValue = !(Boolean.TRUE.equals(member.getLeaderboardOptOut()));
        member.setLeaderboardOptOut(newValue);
        communityMemberRepository.save(member);
        return newValue;
    }
}
