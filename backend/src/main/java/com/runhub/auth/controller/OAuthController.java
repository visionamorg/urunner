package com.runhub.auth.controller;

import com.runhub.auth.dto.AuthResponse;
import com.runhub.auth.service.GarminOAuthService;
import com.runhub.auth.service.StravaOAuthService;
import com.runhub.config.OAuthProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final StravaOAuthService stravaOAuthService;
    private final GarminOAuthService garminOAuthService;
    private final OAuthProperties oAuthProperties;

    // ── Strava ─────────────────────────────────────────────────────────────

    @GetMapping("/strava/connect")
    public void stravaConnect(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();
        String url = stravaOAuthService.buildAuthorizationUrl(state);
        response.sendRedirect(url);
    }

    @GetMapping("/strava/callback")
    public void stravaCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            HttpServletResponse response) throws IOException {

        if (error != null || code == null) {
            log.warn("Strava OAuth denied or error: {}", error);
            response.sendRedirect(oAuthProperties.getFrontendCallbackUrl() + "?error=access_denied");
            return;
        }

        try {
            AuthResponse auth = stravaOAuthService.exchangeCodeAndUpsertUser(code);
            redirectWithToken(response, auth);
        } catch (Exception e) {
            log.error("Strava OAuth callback failed", e);
            response.sendRedirect(oAuthProperties.getFrontendCallbackUrl() + "?error=server_error");
        }
    }

    // ── Garmin ─────────────────────────────────────────────────────────────

    @GetMapping("/garmin/connect")
    public void garminConnect(HttpServletResponse response) throws IOException {
        try {
            String url = garminOAuthService.getAuthorizationUrl();
            response.sendRedirect(url);
        } catch (Exception e) {
            log.error("Failed to initiate Garmin OAuth", e);
            response.sendRedirect(oAuthProperties.getFrontendCallbackUrl() + "?error=server_error");
        }
    }

    @GetMapping("/garmin/callback")
    public void garminCallback(
            @RequestParam(name = "oauth_token", required = false) String oauthToken,
            @RequestParam(name = "oauth_verifier", required = false) String oauthVerifier,
            HttpServletResponse response) throws IOException {

        if (oauthToken == null || oauthVerifier == null) {
            response.sendRedirect(oAuthProperties.getFrontendCallbackUrl() + "?error=access_denied");
            return;
        }

        try {
            AuthResponse auth = garminOAuthService.exchangeTokenAndUpsertUser(oauthToken, oauthVerifier);
            redirectWithToken(response, auth);
        } catch (Exception e) {
            log.error("Garmin OAuth callback failed", e);
            response.sendRedirect(oAuthProperties.getFrontendCallbackUrl() + "?error=server_error");
        }
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private void redirectWithToken(HttpServletResponse response, AuthResponse auth) throws IOException {
        String params = "?token=" + URLEncoder.encode(auth.getToken(), StandardCharsets.UTF_8)
                + "&username=" + URLEncoder.encode(auth.getUsername(), StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(auth.getEmail(), StandardCharsets.UTF_8)
                + "&role=" + auth.getRole()
                + "&userId=" + auth.getUserId()
                + "&provider=" + auth.getProvider();
        response.sendRedirect(oAuthProperties.getFrontendCallbackUrl() + params);
    }
}
