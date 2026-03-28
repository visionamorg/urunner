package com.runhub.communities.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runhub.communities.service.CommunityService;
import com.runhub.programs.service.ProgramService;
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
    private final ProgramService programService;
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
                JsonNode metadata = session.path("metadata");
                String entityType = metadata.path("entity_type").asText("");

                if (email == null) return ResponseEntity.ok().build();

                switch (entityType) {
                    case "PROGRAMME" -> {
                        Long programId = extractLongMeta(metadata, "program_id");
                        if (programId != null) {
                            programService.activateEnrollment(programId, email);
                            log.info("Stripe webhook: activated programme {} for user {}", programId, email);
                        }
                    }
                    default -> {
                        // Legacy: community join payment
                        Long communityId = extractLongMeta(metadata, "community_id");
                        if (communityId != null) {
                            User user = userRepository.findByEmail(email).orElse(null);
                            if (user != null) {
                                communityService.joinCommunity(communityId, user);
                                log.info("Stripe webhook: auto-joined user {} to community {}", email, communityId);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Stripe webhook processing error", e);
        }
        return ResponseEntity.ok().build();
    }

    private Long extractLongMeta(JsonNode metadata, String key) {
        if (metadata.has(key)) {
            try { return Long.parseLong(metadata.path(key).asText()); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
