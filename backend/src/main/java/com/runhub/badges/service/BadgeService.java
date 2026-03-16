package com.runhub.badges.service;

import com.runhub.badges.dto.BadgeDto;
import com.runhub.badges.dto.CreateBadgeRequest;
import com.runhub.badges.dto.UserBadgeDto;
import com.runhub.badges.mapper.BadgeMapper;
import com.runhub.badges.model.Badge;
import com.runhub.badges.model.UserBadge;
import com.runhub.badges.repository.BadgeRepository;
import com.runhub.badges.repository.UserBadgeRepository;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeMapper badgeMapper;
    private final UserService userService;
    private final ActivityRepository activityRepository;

    public List<BadgeDto> getAllBadges() {
        return badgeRepository.findAll().stream().map(badgeMapper::toDto).toList();
    }

    @Transactional
    public BadgeDto createBadge(CreateBadgeRequest request) {
        Badge badge = Badge.builder()
                .name(request.getName())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .criteria(request.getCriteria())
                .build();
        return badgeMapper.toDto(badgeRepository.save(badge));
    }

    public List<UserBadgeDto> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId)
                .stream().map(badgeMapper::toUserBadgeDto).toList();
    }

    public List<UserBadgeDto> getMyBadges(String email) {
        User user = userService.getUserEntityByEmail(email);
        return getUserBadges(user.getId());
    }

    @Transactional
    public void checkAndAwardBadges(Long userId) {
        Double totalDist = activityRepository.sumDistanceByUserId(userId);
        Long totalRuns = activityRepository.countByUserId(userId);

        // Check first run badge
        if (totalRuns >= 1) awardBadgeIfNotHas(userId, "First Run");
        if (totalDist != null) {
            if (totalDist >= 5) awardBadgeIfNotHas(userId, "5K Club");
            if (totalDist >= 10) awardBadgeIfNotHas(userId, "10K Warrior");
            if (totalDist >= 21.1) awardBadgeIfNotHas(userId, "Half Marathoner");
            if (totalDist >= 42.2) awardBadgeIfNotHas(userId, "Marathoner");
            if (totalDist >= 100) awardBadgeIfNotHas(userId, "100 KM Club");
            if (totalDist >= 500) awardBadgeIfNotHas(userId, "500 KM Legend");
        }
    }

    private void awardBadgeIfNotHas(Long userId, String badgeName) {
        badgeRepository.findByName(badgeName).ifPresent(badge -> {
            if (!userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId())) {
                User userRef = new User();
                userRef.setId(userId);
                UserBadge ub = UserBadge.builder()
                        .user(userRef)
                        .badge(badge)
                        .build();
                userBadgeRepository.save(ub);
            }
        });
    }
}
