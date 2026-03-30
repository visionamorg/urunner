package com.runhub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.oauth")
@Data
public class OAuthProperties {

    private StravaProperties strava = new StravaProperties();
    private GarminProperties garmin = new GarminProperties();
    private String frontendCallbackUrl = "http://localhost:4200/oauth/callback";

    @Data
    public static class StravaProperties {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }

    @Data
    public static class GarminProperties {
        private String consumerKey;
        private String consumerSecret;
        private String redirectUri;
        private String webhookSecret = "";
    }
}
