package com.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {

    private GoogleProperties google;
    private GitHubProperties github;
    private CookieProperties cookie;
    private String frontendRedirectUrl;

    @Data
    public static class GoogleProperties {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String authUri;
        private String tokenUri;
        private String userInfoUri;
        private String scopes;
    }

    @Data
    public static class GitHubProperties {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String authUri;
        private String tokenUri;
        private String userInfoUri;
        private String userEmailsUri;
        private String scopes;
    }

    @Data
    public static class CookieProperties {
        private int maxAge;
        private boolean secure;
        private boolean httpOnly;
        private String sameSite;
    }
}
