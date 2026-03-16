package com.runhub.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.apis.GarminApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.runhub.auth.dto.AuthResponse;
import com.runhub.config.JwtService;
import com.runhub.config.OAuthProperties;
import com.runhub.users.model.AuthProvider;
import com.runhub.users.model.Role;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GarminOAuthService {

    private static final String GARMIN_USER_ID_URL = "https://apis.garmin.com/wellness-api/rest/user/id";

    private final OAuthProperties oAuthProperties;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // Temporary store for request tokens keyed by oauth_token value
    private final ConcurrentHashMap<String, OAuth1RequestToken> requestTokenStore = new ConcurrentHashMap<>();

    private OAuth10aService buildService() {
        return new ServiceBuilder(oAuthProperties.getGarmin().getConsumerKey())
                .apiSecret(oAuthProperties.getGarmin().getConsumerSecret())
                .callback(oAuthProperties.getGarmin().getRedirectUri())
                .build(GarminApi.instance());
    }

    public String getAuthorizationUrl() throws Exception {
        OAuth10aService service = buildService();
        OAuth1RequestToken requestToken = service.getRequestToken();
        requestTokenStore.put(requestToken.getToken(), requestToken);
        return service.getAuthorizationUrl(requestToken);
    }

    @Transactional
    public AuthResponse exchangeTokenAndUpsertUser(String oauthToken, String oauthVerifier) throws Exception {
        OAuth1RequestToken requestToken = requestTokenStore.remove(oauthToken);
        if (requestToken == null) {
            throw new IllegalStateException("Unknown or expired OAuth request token");
        }

        OAuth10aService service = buildService();
        OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);

        String garminUserId = fetchGarminUserId(service, accessToken);

        User user = userRepository.findByProviderIdAndAuthProvider(garminUserId, AuthProvider.GARMIN)
                .orElse(null);

        if (user == null) {
            String email = garminUserId + "@garmin.runhub.local";
            String username = generateUniqueUsername("garmin" + garminUserId);
            user = User.builder()
                    .username(username)
                    .email(email)
                    .password("oauth_" + UUID.randomUUID())
                    .firstName("Garmin")
                    .lastName("User")
                    .role(Role.USER)
                    .authProvider(AuthProvider.GARMIN)
                    .providerId(garminUserId)
                    .providerAccessToken(accessToken.getToken())
                    .providerTokenSecret(accessToken.getTokenSecret())
                    .build();
        } else {
            user.setProviderAccessToken(accessToken.getToken());
            user.setProviderTokenSecret(accessToken.getTokenSecret());
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

    public OAuth1AccessToken buildAccessToken(User user) {
        return new OAuth1AccessToken(user.getProviderAccessToken(), user.getProviderTokenSecret());
    }

    public OAuth10aService getService() {
        return buildService();
    }

    private String fetchGarminUserId(OAuth10aService service, OAuth1AccessToken token) throws Exception {
        OAuthRequest request = new OAuthRequest(Verb.GET, GARMIN_USER_ID_URL);
        service.signRequest(token, request);
        try (Response response = service.execute(request)) {
            String body = response.getBody();
            // Response: {"userId":"12345678"}
            return body.replaceAll(".*\"userId\"\\s*:\\s*\"([^\"]+)\".*", "$1");
        }
    }

    private String generateUniqueUsername(String base) {
        String clean = base.replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
        if (clean.length() > 40) clean = clean.substring(0, 40);
        String candidate = clean;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = clean + suffix++;
        }
        return candidate;
    }
}
