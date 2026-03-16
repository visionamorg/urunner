package com.runhub.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runhub.auth.dto.AuthResponse;
import com.runhub.config.JwtService;
import com.runhub.config.OAuthProperties;
import com.runhub.users.model.AuthProvider;
import com.runhub.users.model.Role;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StravaOAuthService {

    private static final String STRAVA_AUTH_URL = "https://www.strava.com/oauth/authorize";
    private static final String STRAVA_TOKEN_URL = "https://www.strava.com/oauth/token";

    private final OAuthProperties oAuthProperties;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public String buildAuthorizationUrl(String state) {
        return UriComponentsBuilder.fromHttpUrl(STRAVA_AUTH_URL)
                .queryParam("client_id", oAuthProperties.getStrava().getClientId())
                .queryParam("redirect_uri", oAuthProperties.getStrava().getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", "activity:read_all,profile:read_all")
                .queryParam("state", state)
                .toUriString();
    }

    @Transactional
    public AuthResponse exchangeCodeAndUpsertUser(String code) {
        JsonNode tokenResponse = exchangeCode(code);

        String accessToken = tokenResponse.get("access_token").asText();
        String refreshToken = tokenResponse.get("refresh_token").asText();
        long expiresAt = tokenResponse.get("expires_at").asLong();

        JsonNode athlete = tokenResponse.get("athlete");
        String stravaId = String.valueOf(athlete.get("id").asLong());
        String firstName = athlete.has("firstname") ? athlete.get("firstname").asText() : "Strava";
        String lastName = athlete.has("lastname") ? athlete.get("lastname").asText("User") : "User";
        String profileImageUrl = athlete.has("profile") ? athlete.get("profile").asText() : null;

        User user = userRepository.findByProviderIdAndAuthProvider(stravaId, AuthProvider.STRAVA)
                .orElseGet(() -> {
                    // Check if email already registered locally; if so link the account
                    String email = athlete.has("email") ? athlete.get("email").asText() : null;
                    if (email != null) {
                        return userRepository.findByEmail(email).orElse(null);
                    }
                    return null;
                });

        if (user == null) {
            String email = athlete.has("email") ? athlete.get("email").asText()
                    : stravaId + "@strava.runhub.local";
            String username = generateUniqueUsername(
                    athlete.has("username") ? athlete.get("username").asText() : firstName.toLowerCase() + lastName.toLowerCase()
            );
            user = User.builder()
                    .username(username)
                    .email(email)
                    .password("oauth_" + UUID.randomUUID())
                    .firstName(firstName)
                    .lastName(lastName)
                    .profileImageUrl(profileImageUrl)
                    .role(Role.USER)
                    .authProvider(AuthProvider.STRAVA)
                    .providerId(stravaId)
                    .providerAccessToken(accessToken)
                    .providerRefreshToken(refreshToken)
                    .providerTokenExpiresAt(expiresAt)
                    .build();
        } else {
            user.setAuthProvider(AuthProvider.STRAVA);
            user.setProviderId(stravaId);
            user.setProviderAccessToken(accessToken);
            user.setProviderRefreshToken(refreshToken);
            user.setProviderTokenExpiresAt(expiresAt);
            if (profileImageUrl != null) user.setProfileImageUrl(profileImageUrl);
        }

        user = userRepository.save(user);
        String jwt = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .username(user.getDisplayUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId())
                .provider(user.getAuthProvider().name())
                .build();
    }

    public String refreshAccessTokenIfNeeded(User user) {
        long nowEpoch = System.currentTimeMillis() / 1000;
        if (user.getProviderTokenExpiresAt() != null && user.getProviderTokenExpiresAt() > nowEpoch + 300) {
            return user.getProviderAccessToken();
        }
        return doRefreshToken(user);
    }

    @Transactional
    public String doRefreshToken(User user) {
        try {
            RestTemplate rt = new RestTemplate();
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", oAuthProperties.getStrava().getClientId());
            body.add("client_secret", oAuthProperties.getStrava().getClientSecret());
            body.add("grant_type", "refresh_token");
            body.add("refresh_token", user.getProviderRefreshToken());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<JsonNode> resp = rt.exchange(
                    STRAVA_TOKEN_URL, HttpMethod.POST,
                    new HttpEntity<>(body, headers), JsonNode.class);

            if (resp.getBody() != null) {
                String newToken = resp.getBody().get("access_token").asText();
                String newRefresh = resp.getBody().get("refresh_token").asText();
                long newExpiry = resp.getBody().get("expires_at").asLong();
                user.setProviderAccessToken(newToken);
                user.setProviderRefreshToken(newRefresh);
                user.setProviderTokenExpiresAt(newExpiry);
                userRepository.save(user);
                return newToken;
            }
        } catch (Exception e) {
            log.error("Failed to refresh Strava token for user {}", user.getId(), e);
        }
        return user.getProviderAccessToken();
    }

    private JsonNode exchangeCode(String code) {
        RestTemplate rt = new RestTemplate();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", oAuthProperties.getStrava().getClientId());
        body.add("client_secret", oAuthProperties.getStrava().getClientSecret());
        body.add("code", code);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<JsonNode> response = rt.exchange(
                STRAVA_TOKEN_URL, HttpMethod.POST,
                new HttpEntity<>(body, headers), JsonNode.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Empty response from Strava token endpoint");
        }
        return response.getBody();
    }

    private String generateUniqueUsername(String base) {
        String clean = base.replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
        if (clean.length() < 3) clean = "runner" + clean;
        if (clean.length() > 40) clean = clean.substring(0, 40);
        String candidate = clean;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = clean + suffix++;
        }
        return candidate;
    }
}
