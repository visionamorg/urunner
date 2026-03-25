package com.runhub.running.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.runhub.auth.service.GarminOAuthService;
import com.runhub.badges.service.BadgeService;
import com.runhub.running.dto.SyncResultDto;
import com.runhub.running.model.ActivitySource;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class GarminSyncService {

    // Garmin Wellness API — last 30 days of activities
    private static final String GARMIN_ACTIVITIES_URL =
            "https://apis.garmin.com/wellness-api/rest/activities";

    private final ActivityRepository activityRepository;
    private final GarminOAuthService garminOAuthService;
    private final BadgeService badgeService;
    private final ObjectMapper objectMapper;

    @Transactional
    public SyncResultDto syncActivities(User user) {
        long endEpoch = Instant.now().getEpochSecond();
        long startEpoch = endEpoch - (30L * 24 * 3600); // last 30 days

        String url = GARMIN_ACTIVITIES_URL
                + "?uploadStartTimeInSeconds=" + startEpoch
                + "&uploadEndTimeInSeconds=" + endEpoch;

        try {
            OAuth10aService service = garminOAuthService.getService();
            OAuthRequest request = new OAuthRequest(Verb.GET, url);
            service.signRequest(garminOAuthService.buildAccessToken(user), request);

            try (Response response = service.execute(request)) {
                if (!response.isSuccessful()) {
                    log.error("Garmin API error {}: {}", response.getCode(), response.getBody());
                    return SyncResultDto.builder()
                            .message("Garmin API returned " + response.getCode())
                            .build();
                }

                JsonNode root = objectMapper.readTree(response.getBody());
                return processActivities(user, root);
            }
        } catch (Exception e) {
            log.error("Garmin sync failed for user {}", user.getId(), e);
            return SyncResultDto.builder().message("Sync failed: " + e.getMessage()).build();
        }
    }

    private SyncResultDto processActivities(User user, JsonNode root) {
        int imported = 0, skipped = 0;

        // Garmin returns: {"activities": [...]} or directly an array
        JsonNode activities = root.isArray() ? root :
                (root.has("activities") ? root.get("activities") : root);

        if (activities == null || !activities.isArray()) {
            return SyncResultDto.builder().message("No activities in response").build();
        }

        for (JsonNode node : activities) {
            // Only running activities
            String activityType = node.has("activityType") ? node.get("activityType").asText() : "";
            if (!activityType.toLowerCase().contains("running") &&
                !activityType.toLowerCase().contains("run")) {
                continue;
            }

            String summaryId = node.has("summaryId") ? node.get("summaryId").asText()
                    : (node.has("activityId") ? node.get("activityId").asText() : null);
            if (summaryId == null) continue;

            String externalId = "garmin_" + summaryId;
            if (activityRepository.existsByExternalId(externalId)) {
                skipped++;
                continue;
            }

            double distanceMeters = node.has("distanceInMeters") ? node.get("distanceInMeters").asDouble()
                    : (node.has("totalDistanceInMeters") ? node.get("totalDistanceInMeters").asDouble() : 0);
            double distanceKm = distanceMeters / 1000.0;
            if (distanceKm < 0.1) continue;

            int durationSec = node.has("durationInSeconds") ? node.get("durationInSeconds").asInt()
                    : (node.has("movingDurationInSeconds") ? node.get("movingDurationInSeconds").asInt() : 0);
            int durationMinutes = Math.max(1, durationSec / 60);

            long startEpoch = node.has("startTimeInSeconds") ? node.get("startTimeInSeconds").asLong()
                    : (node.has("startTimeGMT") ? node.get("startTimeGMT").asLong() : 0);
            LocalDate activityDate = startEpoch > 0
                    ? Instant.ofEpochSecond(startEpoch).atZone(ZoneOffset.UTC).toLocalDate()
                    : LocalDate.now();

            User userRef = new User();
            userRef.setId(user.getId());

            // Telemetry from Garmin summary
            Integer elevationGain = node.has("elevationGainInMeters")
                    ? (int) node.get("elevationGainInMeters").asDouble() : null;
            Integer avgHr = node.has("averageHeartRateInBeatsPerMinute")
                    ? node.get("averageHeartRateInBeatsPerMinute").asInt() : null;
            Integer maxHr = node.has("maxHeartRateInBeatsPerMinute")
                    ? node.get("maxHeartRateInBeatsPerMinute").asInt() : null;
            Integer avgCadence = node.has("averageRunCadenceInStepsPerMinute")
                    ? node.get("averageRunCadenceInStepsPerMinute").asInt() : null;

            RunningActivity activity = RunningActivity.builder()
                    .user(userRef)
                    .title(node.has("activityName") ? node.get("activityName").asText() : "Garmin Run")
                    .distanceKm(distanceKm)
                    .durationMinutes(durationMinutes)
                    .activityDate(activityDate)
                    .source(ActivitySource.GARMIN)
                    .externalId(externalId)
                    .elevationGainMeters(elevationGain)
                    .avgHeartRate(avgHr)
                    .maxHeartRate(maxHr)
                    .avgCadence(avgCadence)
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
}
