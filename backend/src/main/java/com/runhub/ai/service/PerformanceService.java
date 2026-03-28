package com.runhub.ai.service;

import com.runhub.ai.dto.PerformanceDto;
import com.runhub.ai.dto.PerformanceDto.DailyMetric;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final ActivityRepository activityRepository;
    private final UserService userService;

    private static final int CTL_DAYS = 42;
    private static final int ATL_DAYS = 7;

    public PerformanceDto getPerformance(String userEmail) {
        User user = userService.getUserEntityByEmail(userEmail);
        List<RunningActivity> activities = activityRepository.findByUserIdOrderByActivityDateDesc(user.getId());

        if (activities.isEmpty()) {
            return PerformanceDto.builder()
                    .currentCTL(0).currentATL(0).currentTSB(0)
                    .trainingZone("DETRAINING")
                    .history(List.of())
                    .taperSimulation(List.of())
                    .build();
        }

        // Build daily TSS map over the last 90 days
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(90);

        Map<LocalDate, Double> dailyTss = new HashMap<>();
        for (RunningActivity a : activities) {
            if (a.getActivityDate() != null && !a.getActivityDate().isBefore(startDate)) {
                double tss = calculateTSS(a);
                dailyTss.merge(a.getActivityDate(), tss, Double::sum);
            }
        }

        // Calculate EWMA for CTL and ATL over the 90-day window
        List<DailyMetric> history = new ArrayList<>();
        double ctl = 0;
        double atl = 0;
        double lambdaCTL = 2.0 / (CTL_DAYS + 1);
        double lambdaATL = 2.0 / (ATL_DAYS + 1);

        for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
            double tss = dailyTss.getOrDefault(d, 0.0);
            ctl = ctl + lambdaCTL * (tss - ctl);
            atl = atl + lambdaATL * (tss - atl);
            double tsb = ctl - atl;

            history.add(DailyMetric.builder()
                    .date(d)
                    .tss(Math.round(tss * 10.0) / 10.0)
                    .ctl(Math.round(ctl * 10.0) / 10.0)
                    .atl(Math.round(atl * 10.0) / 10.0)
                    .tsb(Math.round(tsb * 10.0) / 10.0)
                    .build());
        }

        double currentCTL = ctl;
        double currentATL = atl;
        double currentTSB = ctl - atl;

        // Taper simulation: simulate 7 days of zero TSS
        List<DailyMetric> taperSim = new ArrayList<>();
        double simCTL = currentCTL;
        double simATL = currentATL;
        for (int i = 1; i <= 7; i++) {
            simCTL = simCTL + lambdaCTL * (0 - simCTL);
            simATL = simATL + lambdaATL * (0 - simATL);
            double simTSB = simCTL - simATL;
            taperSim.add(DailyMetric.builder()
                    .date(today.plusDays(i))
                    .tss(0)
                    .ctl(Math.round(simCTL * 10.0) / 10.0)
                    .atl(Math.round(simATL * 10.0) / 10.0)
                    .tsb(Math.round(simTSB * 10.0) / 10.0)
                    .build());
        }

        String zone = determineTrainingZone(currentTSB, currentCTL);

        return PerformanceDto.builder()
                .currentCTL(Math.round(currentCTL * 10.0) / 10.0)
                .currentATL(Math.round(currentATL * 10.0) / 10.0)
                .currentTSB(Math.round(currentTSB * 10.0) / 10.0)
                .trainingZone(zone)
                .history(history)
                .taperSimulation(taperSim)
                .build();
    }

    /**
     * Calculate Training Stress Score (TSS) for an activity.
     * Simplified formula: TSS = duration_minutes * intensity_factor
     * Intensity is derived from pace relative to a baseline threshold pace.
     */
    private double calculateTSS(RunningActivity activity) {
        double duration = activity.getDurationMinutes() != null ? activity.getDurationMinutes() : 0;
        if (duration <= 0) return 0;

        double intensity = 0.5; // default easy
        Double pace = activity.getPaceMinPerKm();

        if (pace != null && pace > 0) {
            // Assume threshold pace ~5:00/km; faster = higher intensity
            double thresholdPace = 5.0;
            double normalizedPace = thresholdPace / pace; // >1 if faster than threshold
            intensity = Math.min(1.5, Math.max(0.3, normalizedPace));
        }

        // Factor in heart rate if available
        if (activity.getAvgHeartRate() != null && activity.getAvgHeartRate() > 0) {
            double hrFactor = activity.getAvgHeartRate() / 160.0; // assume 160 as threshold HR
            intensity = (intensity + Math.min(1.5, hrFactor)) / 2.0;
        }

        return duration * intensity * intensity; // TSS = duration * IF^2
    }

    private String determineTrainingZone(double tsb, double ctl) {
        if (ctl < 10) return "DETRAINING";
        if (tsb > 15) return "RECOVERY";
        if (tsb >= -10 && tsb <= 15) return "OPTIMAL";
        return "OVERREACHING"; // tsb < -10
    }
}
