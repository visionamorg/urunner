package com.runhub.running.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.runhub.auth.service.GarminOAuthService;
import com.runhub.running.dto.SyncResultDto;
import com.runhub.running.model.HealthMetric;
import com.runhub.running.repository.HealthMetricRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Syncs core health and readiness metrics from the Garmin Wellness API.
 *
 * APIs used:
 *   - Daily summaries: /wellness-api/rest/dailies        (restingHR, bodyBattery, stress)
 *   - Sleep data:      /wellness-api/rest/sleeps         (sleep score)
 *   - User metrics:    /wellness-api/rest/userMetrics    (VO2 max, fitness age)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GarminHealthSyncService {

    private static final String BASE_URL = "https://apis.garmin.com/wellness-api/rest";

    private final GarminOAuthService garminOAuthService;
    private final HealthMetricRepository healthMetricRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public SyncResultDto syncHealthData(User user) {
        long endEpoch = Instant.now().getEpochSecond();
        long startEpoch = endEpoch - (7L * 24 * 3600); // last 7 days

        int saved = 0, skipped = 0;

        try {
            OAuth10aService service = garminOAuthService.getService();

            // 1. Fetch daily summaries (RHR, body battery, stress)
            JsonNode dailies = fetch(service, user, BASE_URL + "/dailies"
                    + "?uploadStartTimeInSeconds=" + startEpoch
                    + "&uploadEndTimeInSeconds=" + endEpoch);

            // 2. Fetch sleep data
            JsonNode sleeps = fetch(service, user, BASE_URL + "/sleeps"
                    + "?uploadStartTimeInSeconds=" + startEpoch
                    + "&uploadEndTimeInSeconds=" + endEpoch);

            // 3. Fetch user metrics (VO2max, fitness age)
            JsonNode userMetrics = fetch(service, user, BASE_URL + "/userMetrics"
                    + "?uploadStartTimeInSeconds=" + startEpoch
                    + "&uploadEndTimeInSeconds=" + endEpoch);

            // Process dailies
            if (dailies != null) {
                JsonNode items = dailies.isArray() ? dailies
                        : (dailies.has("dailies") ? dailies.get("dailies") : null);
                if (items != null && items.isArray()) {
                    for (JsonNode day : items) {
                        LocalDate date = extractDate(day, "calendarDate", "startTimeInSeconds");
                        if (date == null) continue;

                        if (healthMetricRepository.existsByUserIdAndDate(user.getId(), date)) {
                            skipped++;
                            continue;
                        }

                        Integer rhr = day.has("restingHeartRateInBeatsPerMinute")
                                ? day.get("restingHeartRateInBeatsPerMinute").asInt() : null;
                        Integer bodyBattery = day.has("maxBodyBatteryLevel")
                                ? day.get("maxBodyBatteryLevel").asInt() : null;
                        Integer stress = day.has("averageStressLevel")
                                ? day.get("averageStressLevel").asInt() : null;

                        User userRef = new User();
                        userRef.setId(user.getId());

                        HealthMetric metric = HealthMetric.builder()
                                .user(userRef)
                                .date(date)
                                .restingHeartRate(rhr)
                                .bodyBatteryMax(bodyBattery)
                                .stressLevel(stress)
                                .build();

                        healthMetricRepository.save(metric);
                        saved++;
                    }
                }
            }

            // Enrich with sleep scores
            if (sleeps != null) {
                JsonNode items = sleeps.isArray() ? sleeps
                        : (sleeps.has("sleeps") ? sleeps.get("sleeps") : null);
                if (items != null && items.isArray()) {
                    for (JsonNode sleepDay : items) {
                        LocalDate date = extractDate(sleepDay, "calendarDate", "startTimeInSeconds");
                        if (date == null) continue;

                        Integer sleepScore = sleepDay.has("sleepScores")
                                ? sleepDay.get("sleepScores").path("overall").path("value").asInt()
                                : (sleepDay.has("overallSleepScore")
                                        ? sleepDay.get("overallSleepScore").asInt() : null);

                        if (sleepScore != null) {
                            healthMetricRepository.findByUserIdAndDate(user.getId(), date)
                                    .ifPresent(m -> {
                                        m.setSleepScore(sleepScore);
                                        healthMetricRepository.save(m);
                                    });
                        }
                    }
                }
            }

            // Enrich with VO2max / fitness age
            if (userMetrics != null) {
                JsonNode items = userMetrics.isArray() ? userMetrics
                        : (userMetrics.has("userMetrics") ? userMetrics.get("userMetrics") : null);
                if (items != null && items.isArray()) {
                    for (JsonNode um : items) {
                        LocalDate date = extractDate(um, "calendarDate", "startTimeInSeconds");
                        if (date == null) continue;

                        Double vo2max = um.has("vo2Max") ? um.get("vo2Max").asDouble() : null;
                        Integer fitnessAge = um.has("fitnessAge") ? um.get("fitnessAge").asInt() : null;

                        healthMetricRepository.findByUserIdAndDate(user.getId(), date)
                                .ifPresent(m -> {
                                    if (vo2max != null) m.setVo2Max(vo2max);
                                    if (fitnessAge != null) m.setFitnessAge(fitnessAge);
                                    healthMetricRepository.save(m);
                                });
                    }
                }
            }

        } catch (Exception e) {
            log.error("Garmin health sync failed for user {}", user.getId(), e);
            return SyncResultDto.builder().message("Health sync failed: " + e.getMessage()).build();
        }

        return SyncResultDto.builder()
                .imported(saved)
                .skipped(skipped)
                .message("Health sync complete: " + saved + " days saved, " + skipped + " already existed")
                .build();
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private JsonNode fetch(OAuth10aService service, User user, String url) {
        try {
            OAuthRequest request = new OAuthRequest(Verb.GET, url);
            service.signRequest(garminOAuthService.buildAccessToken(user), request);
            try (Response response = service.execute(request)) {
                if (!response.isSuccessful()) {
                    log.warn("Garmin health API {} returned {}", url, response.getCode());
                    return null;
                }
                return objectMapper.readTree(response.getBody());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch Garmin health data from {}: {}", url, e.getMessage());
            return null;
        }
    }

    private LocalDate extractDate(JsonNode node, String calendarField, String epochField) {
        if (node.has(calendarField) && !node.get(calendarField).isNull()) {
            String dateStr = node.get(calendarField).asText();
            try {
                return LocalDate.parse(dateStr);
            } catch (Exception e) {
                // fall through to epoch
            }
        }
        if (node.has(epochField)) {
            long epoch = node.get(epochField).asLong();
            if (epoch > 0) {
                return Instant.ofEpochSecond(epoch).atZone(ZoneOffset.UTC).toLocalDate();
            }
        }
        return null;
    }
}
