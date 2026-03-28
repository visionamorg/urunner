package com.runhub.users.service;

import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StreakFreezeService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public Map<String, Object> getStreak(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return buildStreakResponse(user);
    }

    @Transactional
    public Map<String, Object> updateStreak(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LocalDate> activityDates = activityRepository.findDistinctActivityDatesByUserId(userId);
        if (activityDates.isEmpty()) {
            user.setStreakCount(0);
            user.setLastRunDate(null);
            userRepository.save(user);
            return buildStreakResponse(user);
        }

        // Activity dates are sorted DESC
        LocalDate latest = activityDates.get(0);
        user.setLastRunDate(latest);

        // Calculate streak: count consecutive days backwards from latest
        int streak = 1;
        for (int i = 1; i < activityDates.size(); i++) {
            LocalDate prev = activityDates.get(i - 1);
            LocalDate curr = activityDates.get(i);
            if (prev.minusDays(1).equals(curr)) {
                streak++;
            } else {
                break;
            }
        }

        // If latest activity was not today or yesterday, streak is broken (unless freeze active)
        LocalDate today = LocalDate.now();
        if (!latest.equals(today) && !latest.equals(today.minusDays(1))) {
            if (user.getStreakFreezeActive() && latest.isAfter(today.minusDays(3))) {
                // Freeze covers up to 2 days gap
                user.setStreakFreezeActive(false);
            } else {
                streak = 0;
            }
        }

        user.setStreakCount(streak);

        // Award freeze every 30 days
        int earnedFreezes = streak / 30;
        if (earnedFreezes > user.getStreakFreezes()) {
            user.setStreakFreezes(earnedFreezes);
        }

        userRepository.save(user);
        return buildStreakResponse(user);
    }

    @Transactional
    public Map<String, Object> activateFreeze(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getStreakFreezes() <= 0) {
            throw new RuntimeException("No freezes available");
        }
        user.setStreakFreezes(user.getStreakFreezes() - 1);
        user.setStreakFreezeActive(true);
        userRepository.save(user);
        return buildStreakResponse(user);
    }

    private Map<String, Object> buildStreakResponse(User user) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("streakCount", user.getStreakCount());
        m.put("lastRunDate", user.getLastRunDate() != null ? user.getLastRunDate().toString() : null);
        m.put("streakFreezes", user.getStreakFreezes());
        m.put("freezeActive", user.getStreakFreezeActive());
        m.put("streakAtRisk", isStreakAtRisk(user));
        return m;
    }

    private boolean isStreakAtRisk(User user) {
        if (user.getStreakCount() == 0) return false;
        LocalDate today = LocalDate.now();
        return user.getLastRunDate() != null && !user.getLastRunDate().equals(today);
    }
}
