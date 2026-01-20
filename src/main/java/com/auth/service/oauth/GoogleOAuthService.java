package com.auth.service.oauth;

import com.auth.config.OAuthProperties;
import com.auth.dto.oauth.GoogleUserInfo;
import com.auth.dto.oauth.OAuthTokenResponse;
import com.auth.util.OAuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final OAuthProperties oAuthProperties;
    private final WebClient webClient;

    public String createAuthorizationUrl(String state, String codeVerifier) {
        String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);

        return UriComponentsBuilder.fromUriString(oAuthProperties.getGoogle().getAuthUri())
                .queryParam("client_id", oAuthProperties.getGoogle().getClientId())
                .queryParam("redirect_uri", oAuthProperties.getGoogle().getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", oAuthProperties.getGoogle().getScopes().replace(",", " "))
                .queryParam("state", state)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()
                .toUriString();
    }

    public OAuthTokenResponse exchangeCodeForTokens(String code, String codeVerifier) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", oAuthProperties.getGoogle().getClientId());
        formData.add("client_secret", oAuthProperties.getGoogle().getClientSecret());
        formData.add("code", code);
        formData.add("code_verifier", codeVerifier);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", oAuthProperties.getGoogle().getRedirectUri());

        return webClient
                .post()
                .uri(oAuthProperties.getGoogle().getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OAuthTokenResponse.class)
                .block();
    }

    public GoogleUserInfo getUserInfo(String accessToken) {
        return webClient
                .get()
                .uri(oAuthProperties.getGoogle().getUserInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }
}
