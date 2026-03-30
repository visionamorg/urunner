package com.runhub.running.service;

import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceAnalyticsService {

    private final ActivityRepository activityRepository;

    /**
     * CTL = 42-day exponential moving average of daily TSS
     * ATL = 7-day exponential moving average of daily TSS
     * TSB = CTL - ATL (training stress balance / form)
     *
     * TSS simplified: (distanceKm * 10) + (avgHR != null ? avgHR * 0.1 * durationMinutes / 60 : 0)
     */
    public Map<String, Object> getAthleteLoadMetrics(Long userId, LocalDate from, LocalDate to) {
        if (to == null) to = LocalDate.now();
        if (from == null) from = to.minusDays(30);

        // Load 90 days of history to warm up the EMA
        final LocalDate finalTo = to;
        final LocalDate finalFrom = from;
        LocalDate historyStart = finalTo.minusDays(90);
        List<RunningActivity> allActivities = activityRepository.findByUserIdOrderByActivityDateDesc(userId);

        // Group by date → sum TSS
        Map<LocalDate, Double> dailyTss = allActivities.stream()
                .filter(a -> !a.getActivityDate().isBefore(historyStart) && !a.getActivityDate().isAfter(finalTo))
                .collect(Collectors.groupingBy(
                        RunningActivity::getActivityDate,
                        Collectors.summingDouble(this::computeTss)
                ));

        // Build sorted list of dates from historyStart to to
        List<LocalDate> dateRange = buildDateRange(historyStart, to);

        double ctlFactor = 2.0 / (42.0 + 1.0);
        double atlFactor = 2.0 / (7.0 + 1.0);

        double ctl = 0.0;
        double atl = 0.0;

        List<Map<String, Object>> trend = new ArrayList<>();

        for (LocalDate date : dateRange) {
            double tss = dailyTss.getOrDefault(date, 0.0);
            ctl = ctl + (tss - ctl) * ctlFactor;
            atl = atl + (tss - atl) * atlFactor;
            double tsb = ctl - atl;

            // Only include trend data from the requested 'from' date
            if (!date.isBefore(finalFrom)) {
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("date", date.toString());
                point.put("ctl", Math.round(ctl * 10.0) / 10.0);
                point.put("atl", Math.round(atl * 10.0) / 10.0);
                point.put("tsb", Math.round(tsb * 10.0) / 10.0);
                trend.add(point);
            }
        }

        double finalTsb = ctl - atl;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", userId);
        result.put("ctl", Math.round(ctl * 10.0) / 10.0);
        result.put("atl", Math.round(atl * 10.0) / 10.0);
        result.put("tsb", Math.round(finalTsb * 10.0) / 10.0);
        result.put("trend", trend);
        return result;
    }

    public List<Map<String, Object>> getTeamLoadMetrics(List<Long> athleteIds) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(30);
        return athleteIds.stream()
                .map(id -> getAthleteLoadMetrics(id, from, to))
                .collect(Collectors.toList());
    }

    private double computeTss(RunningActivity a) {
        double tss = a.getDistanceKm() * 10.0;
        if (a.getAvgHeartRate() != null) {
            tss += a.getAvgHeartRate() * 0.1 * a.getDurationMinutes() / 60.0;
        }
        return tss;
    }

    private List<LocalDate> buildDateRange(LocalDate from, LocalDate to) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }
}
