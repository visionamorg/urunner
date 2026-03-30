package com.runhub.running.controller;

import com.runhub.config.OAuthProperties;
import com.runhub.running.service.GarminWebhookProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Slf4j
@RestController
@RequestMapping("/api/webhooks/garmin")
@RequiredArgsConstructor
public class GarminWebhookController {

    private final GarminWebhookProcessor webhookProcessor;
    private final OAuthProperties oAuthProperties;

    /**
     * Receives Garmin push notifications for activity summaries,
     * deRegistrations, and userConsentStatus events.
     * Returns 200 immediately; all processing is done asynchronously.
     */
    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Garmin-Signature", required = false) String signature) {

        // Verify HMAC-SHA1 signature if webhook-secret is configured
        String secret = oAuthProperties.getGarmin().getWebhookSecret();
        if (secret != null && !secret.isBlank()) {
            if (!verifySignature(rawBody, signature, secret)) {
                log.warn("Garmin webhook signature verification failed");
                return ResponseEntity.status(401).build();
            }
        }

        // Delegate to async processor — returns 200 immediately
        webhookProcessor.process(rawBody);

        return ResponseEntity.ok().build();
    }

    private boolean verifySignature(String body, String signature, String secret) {
        if (signature == null || signature.isBlank()) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] digest = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
            String computed = HexFormat.of().formatHex(digest);
            // Garmin sends "sha1=<hex>"
            String expected = signature.startsWith("sha1=") ? signature.substring(5) : signature;
            return computed.equalsIgnoreCase(expected);
        } catch (Exception e) {
            log.error("HMAC verification error", e);
            return false;
        }
    }
}
