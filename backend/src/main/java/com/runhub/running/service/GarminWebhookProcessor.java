package com.runhub.running.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runhub.running.model.LiveTrackingSession;
import com.runhub.running.repository.LiveTrackingRepository;
import com.runhub.users.model.AuthProvider;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Async delegate that processes Garmin webhook payloads off the HTTP request thread.
 * Must be a separate Spring bean from GarminWebhookController for @Async proxy to work.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GarminWebhookProcessor {

    private final GarminSyncService garminSyncService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final LiveTrackingRepository liveTrackingRepository;

    @Async
    public void process(String rawBody) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);

            // Handle activitySummaries
            if (root.has("activitySummaries")) {
                for (JsonNode wrapper : root.get("activitySummaries")) {
                    String garminUserId = wrapper.has("userId") ? wrapper.get("userId").asText() : null;
                    JsonNode summary = wrapper.has("summary") ? wrapper.get("summary") : wrapper;

                    if (garminUserId != null) {
                        garminSyncService.processWebhookActivity(garminUserId, summary);

                        // G007: check for liveTrackingUrl in the summary
                        if (summary.has("liveTrackingUrl")) {
                            String liveTrackUrl = summary.get("liveTrackingUrl").asText();
                            saveLiveTrackUrl(garminUserId, liveTrackUrl);
                        }
                    }
                }
            }

            // Handle deRegistrations — clear tokens
            if (root.has("deRegistrations")) {
                for (JsonNode wrapper : root.get("deRegistrations")) {
                    String garminUserId = wrapper.has("userId") ? wrapper.get("userId").asText() : null;
                    if (garminUserId != null) {
                        userRepository.findByProviderIdAndAuthProvider(garminUserId, AuthProvider.GARMIN)
                                .ifPresent(u -> {
                                    u.setProviderAccessToken(null);
                                    u.setProviderTokenSecret(null);
                                    userRepository.save(u);
                                    log.info("Garmin deRegistration for userId={}", garminUserId);
                                });
                    }
                }
            }

            // Handle userConsentStatus — log only
            if (root.has("userConsentStatus")) {
                for (JsonNode status : root.get("userConsentStatus")) {
                    log.info("Garmin userConsentStatus: userId={} consentStatus={}",
                            status.path("userId").asText(),
                            status.path("consentStatus").asText());
                }
            }

        } catch (Exception e) {
            log.error("Error processing Garmin webhook payload", e);
        }
    }

    private void saveLiveTrackUrl(String garminUserId, String liveTrackUrl) {
        try {
            Optional<User> userOpt = userRepository.findByProviderIdAndAuthProvider(garminUserId, AuthProvider.GARMIN);
            if (userOpt.isEmpty()) return;
            User user = userOpt.get();

            LiveTrackingSession session = liveTrackingRepository.findByUserIdAndActiveTrue(user.getId())
                    .orElseGet(() -> {
                        String token = UUID.randomUUID().toString().replace("-", "");
                        return LiveTrackingSession.builder()
                                .user(user)
                                .token(token)
                                .active(true)
                                .build();
                    });

            session.setGarminLiveTrackUrl(liveTrackUrl);
            session.setLastUpdate(LocalDateTime.now());
            liveTrackingRepository.save(session);
            log.info("Saved Garmin liveTrackUrl for user {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to save Garmin liveTrackUrl for garminUserId={}", garminUserId, e);
        }
    }
}
