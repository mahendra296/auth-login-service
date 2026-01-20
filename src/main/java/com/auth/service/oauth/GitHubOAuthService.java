package com.auth.service.oauth;

import com.auth.config.OAuthProperties;
import com.auth.dto.oauth.GitHubEmail;
import com.auth.dto.oauth.GitHubUserInfo;
import com.auth.dto.oauth.OAuthTokenResponse;
import java.util.List;
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
public class GitHubOAuthService {

    private final OAuthProperties oAuthProperties;
    private final WebClient webClient;

    public String createAuthorizationUrl(String state) {
        return UriComponentsBuilder.fromUriString(oAuthProperties.getGithub().getAuthUri())
                .queryParam("client_id", oAuthProperties.getGithub().getClientId())
                .queryParam("redirect_uri", oAuthProperties.getGithub().getRedirectUri())
                .queryParam("scope", oAuthProperties.getGithub().getScopes().replace(",", " "))
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    public OAuthTokenResponse exchangeCodeForTokens(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", oAuthProperties.getGithub().getClientId());
        formData.add("client_secret", oAuthProperties.getGithub().getClientSecret());
        formData.add("code", code);
        formData.add("redirect_uri", oAuthProperties.getGithub().getRedirectUri());

        return webClient
                .post()
                .uri(oAuthProperties.getGithub().getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OAuthTokenResponse.class)
                .block();
    }

    public GitHubUserInfo getUserInfo(String accessToken) {
        return webClient
                .get()
                .uri(oAuthProperties.getGithub().getUserInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GitHubUserInfo.class)
                .block();
    }

    public List<GitHubEmail> getUserEmails(String accessToken) {
        return webClient
                .get()
                .uri(oAuthProperties.getGithub().getUserEmailsUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(GitHubEmail.class)
                .collectList()
                .block();
    }

    public String getPrimaryEmail(String accessToken) {
        List<GitHubEmail> emails = getUserEmails(accessToken);
        return emails.stream()
                .filter(GitHubEmail::isPrimary)
                .findFirst()
                .map(GitHubEmail::getEmail)
                .orElse(null);
    }
}
