package com.runhub.running.service;

import com.runhub.running.dto.StreakDto;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final ActivityRepository activityRepository;
    private final UserService userService;

    public StreakDto getStreak(String email) {
        User user = userService.getUserEntityByEmail(email);
        List<LocalDate> dates = activityRepository.findDistinctActivityDatesByUserId(user.getId());

        if (dates.isEmpty()) {
            return StreakDto.builder().currentStreak(0).longestStreak(0).activeToday(false).totalActiveDays(0).build();
        }

        LocalDate today = LocalDate.now();
        boolean activeToday = dates.contains(today);

        // Calculate current streak (consecutive days ending today or yesterday)
        LocalDate cursor = activeToday ? today : today.minusDays(1);
        int current = 0;
        for (LocalDate d : dates) {
            if (d.equals(cursor)) {
                current++;
                cursor = cursor.minusDays(1);
            } else if (d.isBefore(cursor)) {
                break;
            }
        }

        // Calculate longest streak across all history
        int longest = 0;
        int run = 1;
        for (int i = 0; i < dates.size() - 1; i++) {
            if (dates.get(i).minusDays(1).equals(dates.get(i + 1))) {
                run++;
            } else {
                longest = Math.max(longest, run);
                run = 1;
            }
        }
        longest = Math.max(longest, run);

        return StreakDto.builder()
                .currentStreak(current)
                .longestStreak(longest)
                .activeToday(activeToday)
                .totalActiveDays(dates.size())
                .build();
    }
}
