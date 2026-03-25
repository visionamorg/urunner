package com.runhub.communities.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runhub.communities.service.CommunityService;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe-webhook")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final CommunityService communityService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload) {
        try {
            JsonNode event = objectMapper.readTree(payload);
            String type = event.path("type").asText();

            if ("checkout.session.completed".equals(type)) {
                JsonNode session = event.path("data").path("object");
                String email = session.path("customer_email").asText(null);
                Long communityId = extractCommunityId(session);

                if (email != null && communityId != null) {
                    User user = userRepository.findByEmail(email).orElse(null);
                    if (user != null) {
                        communityService.joinCommunity(communityId, user);
                        log.info("Stripe webhook: auto-joined user {} to community {}", email, communityId);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Stripe webhook processing error", e);
        }
        return ResponseEntity.ok().build();
    }

    private Long extractCommunityId(JsonNode session) {
        // Community ID passed via Stripe metadata
        JsonNode metadata = session.path("metadata");
        if (metadata.has("community_id")) {
            try { return Long.parseLong(metadata.path("community_id").asText()); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
