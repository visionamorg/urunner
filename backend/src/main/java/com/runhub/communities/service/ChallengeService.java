package com.runhub.communities.service;

import com.runhub.badges.service.BadgeService;
import com.runhub.communities.model.*;
import com.runhub.communities.repository.*;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final CommunityMemberRepository memberRepository;
    private final ActivityRepository activityRepository;
    private final UserService userService;
    private final BadgeService badgeService;

    public List<Map<String, Object>> getChallenges(Long communityId) {
        return challengeRepository.findByCommunityIdOrderByCreatedAtDesc(communityId)
                .stream().map(this::toMap).toList();
    }

    @Transactional
    public Map<String, Object> createChallenge(Long communityId, Map<String, Object> req, String email) {
        User user = userService.getUserEntityByEmail(email);
        Community community = new Community();
        community.setId(communityId);

        Challenge challenge = Challenge.builder()
                .community(community)
                .title((String) req.get("title"))
                .description((String) req.getOrDefault("description", ""))
                .targetType((String) req.getOrDefault("targetType", "DISTANCE"))
                .targetValue(Double.parseDouble(req.get("targetValue").toString()))
                .startDate(LocalDate.parse((String) req.get("startDate")))
                .endDate(LocalDate.parse((String) req.get("endDate")))
                .build();
        challenge = challengeRepository.save(challenge);

        // Auto-join creator
        ChallengeParticipant cp = ChallengeParticipant.builder()
                .challenge(challenge)
                .user(user)
                .build();
        participantRepository.save(cp);
        challenge.setParticipantCount(1);
        challengeRepository.save(challenge);

        return toMap(challenge);
    }

    @Transactional
    public Map<String, Object> joinChallenge(Long challengeId, String email) {
        User user = userService.getUserEntityByEmail(email);
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (participantRepository.existsByChallengeIdAndUserId(challengeId, user.getId())) {
            throw new RuntimeException("Already joined");
        }

        ChallengeParticipant cp = ChallengeParticipant.builder()
                .challenge(challenge)
                .user(user)
                .build();
        participantRepository.save(cp);

        challenge.setParticipantCount(challenge.getParticipantCount() + 1);
        challengeRepository.save(challenge);

        return toMap(challenge);
    }

    @Transactional
    public Map<String, Object> refreshProgress(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        List<Long> participantIds = participantRepository.findUserIdsByChallengeId(challengeId);
        if (participantIds.isEmpty()) return toMap(challenge);

        double total = 0;
        String type = challenge.getTargetType();

        if ("DISTANCE".equals(type)) {
            total = activityRepository.sumDistanceByUserIdsAndDateRange(
                    participantIds, challenge.getStartDate(), challenge.getEndDate());
        } else if ("RUNS".equals(type)) {
            for (Long uid : participantIds) {
                total += activityRepository.countByUserId(uid);
            }
        } else if ("ELEVATION".equals(type)) {
            // Sum elevation for participants in date range - use simple count for now
            total = activityRepository.sumDistanceByUserIdsAndDateRange(
                    participantIds, challenge.getStartDate(), challenge.getEndDate());
        }

        challenge.setCurrentValue(total);

        // Update individual contributions
        for (Long uid : participantIds) {
            participantRepository.findByChallengeIdAndUserId(challengeId, uid).ifPresent(cp -> {
                double contrib = activityRepository.sumDistanceByUserIdAndDateRange(
                        uid, challenge.getStartDate(), challenge.getEndDate());
                cp.setContribution(contrib);
                participantRepository.save(cp);
            });
        }

        // Check completion
        if (total >= challenge.getTargetValue() && "ACTIVE".equals(challenge.getStatus())) {
            challenge.setStatus("COMPLETED");
            // Award badge to all participants
            for (Long uid : participantIds) {
                badgeService.checkAndAwardBadges(uid);
            }
        }

        // Check expiration
        if (LocalDate.now().isAfter(challenge.getEndDate()) && "ACTIVE".equals(challenge.getStatus())) {
            challenge.setStatus("EXPIRED");
        }

        challengeRepository.save(challenge);
        return toMap(challenge);
    }

    public List<Map<String, Object>> getLeaderboard(Long challengeId) {
        return participantRepository.findByChallengeIdOrderByContributionDesc(challengeId)
                .stream().map(cp -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("userId", cp.getUser().getId());
                    m.put("username", cp.getUser().getUsername());
                    m.put("profileImageUrl", cp.getUser().getProfileImageUrl());
                    m.put("contribution", cp.getContribution());
                    return m;
                }).toList();
    }

    public boolean isParticipant(Long challengeId, String email) {
        User user = userService.getUserEntityByEmail(email);
        return participantRepository.existsByChallengeIdAndUserId(challengeId, user.getId());
    }

    private Map<String, Object> toMap(Challenge c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("title", c.getTitle());
        m.put("description", c.getDescription());
        m.put("targetType", c.getTargetType());
        m.put("targetValue", c.getTargetValue());
        m.put("currentValue", c.getCurrentValue());
        m.put("startDate", c.getStartDate().toString());
        m.put("endDate", c.getEndDate().toString());
        m.put("status", c.getStatus());
        m.put("participantCount", c.getParticipantCount());
        m.put("progress", c.getTargetValue() > 0 ? Math.min(100, (c.getCurrentValue() / c.getTargetValue()) * 100) : 0);
        return m;
    }
}
