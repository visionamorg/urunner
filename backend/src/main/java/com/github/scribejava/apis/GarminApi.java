package com.github.scribejava.apis;

import com.github.scribejava.core.builder.api.DefaultApi10a;

public class GarminApi extends DefaultApi10a {

    private static final String REQUEST_TOKEN_URL =
            "https://connectapi.garmin.com/oauth-service/oauth/request_token";
    private static final String ACCESS_TOKEN_URL =
            "https://connectapi.garmin.com/oauth-service/oauth/access_token";
    private static final String AUTHORIZATION_URL =
            "https://connect.garmin.com/oauthConfirm?oauth_token=%s";

    private GarminApi() {
    }

    private static class InstanceHolder {
        private static final GarminApi INSTANCE = new GarminApi();
    }

    public static GarminApi instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return REQUEST_TOKEN_URL;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_URL;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return AUTHORIZATION_URL;
    }
}
