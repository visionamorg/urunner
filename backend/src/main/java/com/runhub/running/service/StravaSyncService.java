package com.runhub.running.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.runhub.auth.service.StravaOAuthService;
import com.runhub.badges.service.BadgeService;
import com.runhub.running.dto.SyncResultDto;
import com.runhub.running.model.ActivitySource;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StravaSyncService {

    private static final String STRAVA_ACTIVITIES_URL =
            "https://www.strava.com/api/v3/athlete/activities?per_page=200&page=1";

    private final ActivityRepository activityRepository;
    private final StravaOAuthService stravaOAuthService;
    private final BadgeService badgeService;

    @Transactional
    public SyncResultDto syncActivities(User user) {
        String accessToken = stravaOAuthService.refreshAccessTokenIfNeeded(user);

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        ResponseEntity<JsonNode> response = rt.exchange(
                STRAVA_ACTIVITIES_URL, HttpMethod.GET,
                new HttpEntity<>(headers), JsonNode.class);

        if (response.getBody() == null || !response.getBody().isArray()) {
            return SyncResultDto.builder().message("No activities returned from Strava").build();
        }

        int imported = 0, skipped = 0;

        for (JsonNode node : response.getBody()) {
            String type = node.has("type") ? node.get("type").asText() : "";
            // Only sync runs
            if (!"Run".equalsIgnoreCase(type) && !"VirtualRun".equalsIgnoreCase(type)) {
                continue;
            }

            String externalId = "strava_" + node.get("id").asLong();
            if (activityRepository.existsByExternalId(externalId)) {
                skipped++;
                continue;
            }

            double distanceMeters = node.has("distance") ? node.get("distance").asDouble() : 0;
            double distanceKm = distanceMeters / 1000.0;
            if (distanceKm < 0.1) continue;

            int movingTimeSec = node.has("moving_time") ? node.get("moving_time").asInt() : 0;
            int durationMinutes = Math.max(1, movingTimeSec / 60);

            LocalDate activityDate = parseDate(node.has("start_date_local")
                    ? node.get("start_date_local").asText() : null);

            String location = buildLocation(node);

            User userRef = new User();
            userRef.setId(user.getId());

            RunningActivity activity = RunningActivity.builder()
                    .user(userRef)
                    .title(node.has("name") ? node.get("name").asText() : "Strava Run")
                    .distanceKm(distanceKm)
                    .durationMinutes(durationMinutes)
                    .activityDate(activityDate)
                    .location(location)
                    .source(ActivitySource.STRAVA)
                    .externalId(externalId)
                    .build();

            activityRepository.save(activity);
            imported++;
        }

        if (imported > 0) {
            badgeService.checkAndAwardBadges(user.getId());
        }

        return SyncResultDto.builder()
                .imported(imported)
                .skipped(skipped)
                .message("Sync complete: " + imported + " imported, " + skipped + " already existed")
                .build();
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null) return LocalDate.now();
        try {
            return OffsetDateTime.parse(dateStr).toLocalDate();
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateStr.substring(0, 10));
            } catch (Exception ex) {
                return LocalDate.now();
            }
        }
    }

    private String buildLocation(JsonNode node) {
        String city = node.has("location_city") && !node.get("location_city").isNull()
                ? node.get("location_city").asText() : null;
        String country = node.has("location_country") && !node.get("location_country").isNull()
                ? node.get("location_country").asText() : null;
        if (city != null && country != null) return city + ", " + country;
        if (city != null) return city;
        if (country != null) return country;
        return null;
    }
}
