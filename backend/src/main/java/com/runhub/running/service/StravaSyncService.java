package com.runhub.running.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.runhub.auth.service.StravaOAuthService;
import com.runhub.badges.service.BadgeService;
import com.runhub.running.dto.SyncResultDto;
import com.runhub.running.model.ActivitySource;
import com.runhub.running.model.ActivitySplit;
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
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class StravaSyncService {

    private static final String STRAVA_ACTIVITIES_URL =
            "https://www.strava.com/api/v3/athlete/activities?per_page=200&page=1";
    private static final String STRAVA_ACTIVITY_DETAIL_URL =
            "https://www.strava.com/api/v3/activities/";

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

            // Extract telemetry from summary
            Integer elevationGain = node.has("total_elevation_gain")
                    ? (int) node.get("total_elevation_gain").asDouble() : null;
            Integer avgHr = node.has("average_heartrate")
                    ? (int) node.get("average_heartrate").asDouble() : null;
            Integer maxHr = node.has("max_heartrate")
                    ? (int) node.get("max_heartrate").asDouble() : null;
            Integer avgCadence = node.has("average_cadence")
                    ? (int) (node.get("average_cadence").asDouble() * 2) : null; // Strava reports half-cadence

            // Extract summary polyline from list endpoint
            String polyline = null;
            if (node.has("map") && node.get("map").has("summary_polyline")
                    && !node.get("map").get("summary_polyline").isNull()) {
                polyline = node.get("map").get("summary_polyline").asText();
            }

            RunningActivity activity = RunningActivity.builder()
                    .user(userRef)
                    .title(node.has("name") ? node.get("name").asText() : "Strava Run")
                    .distanceKm(distanceKm)
                    .durationMinutes(durationMinutes)
                    .activityDate(activityDate)
                    .location(location)
                    .source(ActivitySource.STRAVA)
                    .externalId(externalId)
                    .elevationGainMeters(elevationGain)
                    .avgHeartRate(avgHr)
                    .maxHeartRate(maxHr)
                    .avgCadence(avgCadence)
                    .mapPolyline(polyline)
                    .build();

            // Fetch detailed activity for full polyline + splits
            enrichFromDetail(activity, node.get("id").asLong(), accessToken, rt, headers);

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

    private void enrichFromDetail(RunningActivity activity, long stravaId,
                                   String accessToken, RestTemplate rt, HttpHeaders headers) {
        try {
            ResponseEntity<JsonNode> detailResp = rt.exchange(
                    STRAVA_ACTIVITY_DETAIL_URL + stravaId,
                    HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            JsonNode detail = detailResp.getBody();
            if (detail == null) return;

            // Full polyline (higher resolution than summary)
            if (detail.has("map") && detail.get("map").has("polyline")
                    && !detail.get("map").get("polyline").isNull()) {
                String fullPolyline = detail.get("map").get("polyline").asText();
                if (!fullPolyline.isEmpty()) {
                    activity.setMapPolyline(fullPolyline);
                }
            }

            // Splits (metric km)
            if (detail.has("splits_metric") && detail.get("splits_metric").isArray()) {
                var splits = new ArrayList<ActivitySplit>();
                int km = 1;
                for (JsonNode s : detail.get("splits_metric")) {
                    double splitMovingTime = s.has("moving_time") ? s.get("moving_time").asDouble() : 0;
                    double splitDistance = s.has("distance") ? s.get("distance").asDouble() : 1000;
                    double splitPace = splitDistance > 0 ? (splitMovingTime / 60.0) / (splitDistance / 1000.0) : 0;

                    ActivitySplit split = ActivitySplit.builder()
                            .activity(activity)
                            .splitKm(km++)
                            .splitPace(Math.round(splitPace * 100.0) / 100.0)
                            .splitElevation(s.has("elevation_difference") ? s.get("elevation_difference").asDouble() : null)
                            .splitHeartRate(s.has("average_heartrate") ? (int) s.get("average_heartrate").asDouble() : null)
                            .build();
                    splits.add(split);
                }
                activity.setSplits(splits);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch Strava detail for activity {}: {}", stravaId, e.getMessage());
        }
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
