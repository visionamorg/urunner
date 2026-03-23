package com.runhub.running.service;

import com.runhub.running.dto.ActivityDto;
import com.runhub.running.dto.ActivityStatsDto;
import com.runhub.running.dto.CreateActivityRequest;
import com.runhub.running.mapper.ActivityMapper;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final UserService userService;

    public List<ActivityDto> getAllActivities() {
        return activityRepository.findAllByOrderByActivityDateDesc()
                .stream().map(activityMapper::toDto).toList();
    }

    public Optional<ActivityDto> getActivityById(Long id, String email) {
        User user = userService.getUserEntityByEmail(email);
        return activityRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .map(activityMapper::toDto);
    }

    public List<ActivityDto> getUserActivities(String email) {
        User user = userService.getUserEntityByEmail(email);
        return activityRepository.findByUserIdOrderByActivityDateDesc(user.getId())
                .stream().map(activityMapper::toDto).toList();
    }

    @Transactional
    public ActivityDto createActivity(String email, CreateActivityRequest request) {
        User user = userService.getUserEntityByEmail(email);
        RunningActivity activity = RunningActivity.builder()
                .user(user)
                .title(request.getTitle())
                .distanceKm(request.getDistanceKm())
                .durationMinutes(request.getDurationMinutes())
                .activityDate(request.getActivityDate() != null ? request.getActivityDate() : LocalDate.now())
                .location(request.getLocation())
                .notes(request.getNotes())
                .build();
        ActivityDto saved = activityMapper.toDto(activityRepository.save(activity));

        // Award RunPoints: 10 pts/km — anti-cheat: skip if pace < 1.5 min/km (superhuman)
        if (request.getDistanceKm() > 0 && request.getDurationMinutes() > 0) {
            double pace = request.getDurationMinutes() / request.getDistanceKm();
            if (pace >= 1.5) {
                int points = (int) (request.getDistanceKm() * 10);
                userService.awardRunPoints(user, points);
            }
        }
        return saved;
    }

    public ActivityStatsDto getUserStats(String email) {
        User user = userService.getUserEntityByEmail(email);
        Long userId = user.getId();

        Double totalDist = activityRepository.sumDistanceByUserId(userId);
        Long totalRuns = activityRepository.countByUserId(userId);
        Long totalDuration = activityRepository.sumDurationByUserId(userId);

        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.minusDays(7);
        LocalDate monthStart = now.minusDays(30);

        Double weeklyDist = activityRepository.sumDistanceByUserIdAndDateRange(userId, weekStart, now);
        Double monthlyDist = activityRepository.sumDistanceByUserIdAndDateRange(userId, monthStart, now);

        double avgPace = (totalDist != null && totalDist > 0 && totalDuration != null)
                ? totalDuration.doubleValue() / totalDist : 0;

        return ActivityStatsDto.builder()
                .totalDistanceKm(totalDist)
                .totalRuns(totalRuns)
                .totalDurationMinutes(totalDuration)
                .avgPaceMinPerKm(avgPace)
                .weeklyDistanceKm(weeklyDist)
                .monthlyDistanceKm(monthlyDist)
                .build();
    }
}
