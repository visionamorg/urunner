package com.runhub.communities.service;

import com.runhub.communities.dto.CommunityGoalDto;
import com.runhub.communities.dto.CreateGoalRequest;
import com.runhub.communities.model.CommunityGoal;
import com.runhub.communities.repository.CommunityGoalRepository;
import com.runhub.communities.repository.CommunityMemberRepository;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityGoalService {

    private final CommunityGoalRepository goalRepository;
    private final CommunityMemberRepository memberRepository;
    private final ActivityRepository activityRepository;

    public Optional<CommunityGoalDto> getCurrentGoal(Long communityId) {
        return goalRepository.findTopByCommunityIdOrderByCreatedAtDesc(communityId)
                .map(g -> toDto(g, communityId));
    }

    public CommunityGoalDto createGoal(Long communityId, CreateGoalRequest req, User admin) {
        CommunityGoal goal = new CommunityGoal();
        goal.setCommunityId(communityId);
        goal.setTitle(req.getTitle());
        goal.setTargetKm(req.getTargetKm());
        goal.setStartDate(req.getStartDate() != null ? req.getStartDate() : LocalDate.now());
        goal.setEndDate(req.getEndDate() != null ? req.getEndDate() : LocalDate.now().plusDays(30));
        goal.setCreatedBy(admin.getId());
        return toDto(goalRepository.save(goal), communityId);
    }

    private CommunityGoalDto toDto(CommunityGoal g, Long communityId) {
        List<Long> memberIds = memberRepository.findByCommunityId(communityId)
                .stream().map(m -> m.getUser().getId()).toList();

        double progress = 0;
        if (!memberIds.isEmpty()) {
            Double sum = activityRepository.sumDistanceByUserIdsAndDateRange(memberIds, g.getStartDate(), g.getEndDate());
            progress = sum != null ? sum : 0;
        }

        double percent = g.getTargetKm() > 0 ? Math.min(100.0, progress / g.getTargetKm() * 100) : 0;
        LocalDate today = LocalDate.now();
        boolean active = !today.isBefore(g.getStartDate()) && !today.isAfter(g.getEndDate());

        return CommunityGoalDto.builder()
                .id(g.getId())
                .title(g.getTitle())
                .targetKm(g.getTargetKm())
                .progressKm(Math.round(progress * 10.0) / 10.0)
                .progressPercent(Math.round(percent * 10.0) / 10.0)
                .startDate(g.getStartDate())
                .endDate(g.getEndDate())
                .active(active)
                .completed(progress >= g.getTargetKm())
                .build();
    }
}
