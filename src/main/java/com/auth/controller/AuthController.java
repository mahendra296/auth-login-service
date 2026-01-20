package com.auth.controller;

import com.auth.config.OAuthProperties;
import com.auth.dto.ApiResponse;
import com.auth.dto.UserResponse;
import com.auth.dto.oauth.GitHubUserInfo;
import com.auth.dto.oauth.GoogleUserInfo;
import com.auth.dto.oauth.OAuthTokenResponse;
import com.auth.dto.oauth.OAuthUrlResponse;
import com.auth.enums.AuthProvider;
import com.auth.service.UserService;
import com.auth.service.oauth.GitHubOAuthService;
import com.auth.service.oauth.GoogleOAuthService;
import com.auth.service.oauth.OAuthStateStore;
import com.auth.util.OAuthUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleOAuthService googleOAuthService;
    private final GitHubOAuthService gitHubOAuthService;
    private final UserService userService;
    private final OAuthProperties oAuthProperties;
    private final OAuthStateStore oAuthStateStore;

    @GetMapping("/google")
    public void initiateGoogleLogin(HttpServletResponse response) throws IOException {
        String state = OAuthUtils.generateState();
        String codeVerifier = OAuthUtils.generateCodeVerifier();

        // Store state and code verifier in memory store
        oAuthStateStore.saveState(state, codeVerifier);

        String authorizationUrl = googleOAuthService.createAuthorizationUrl(state, codeVerifier);
        log.debug("Initiating Google login with state: {}", state);
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/google/callback")
    public void handleGoogleCallback(
            @RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response)
            throws IOException {

        try {
            log.debug("Google callback received with state: {}", state);

            // Validate state from memory store
            if (!oAuthStateStore.isValidState(state)) {
                log.error("Invalid or expired OAuth state: {}", state);
                response.sendRedirect(oAuthProperties.getFrontendRedirectUrl() + "/login?error=invalid_state");
                return;
            }

            String codeVerifier = oAuthStateStore.getCodeVerifier(state);
            log.debug("Retrieved code verifier for state: {}", state);

            OAuthTokenResponse tokens = googleOAuthService.exchangeCodeForTokens(code, codeVerifier);
            GoogleUserInfo googleUser = googleOAuthService.getUserInfo(tokens.getAccessToken());

            UserResponse userResponse = userService.processOAuthUser(
                    googleUser.getEmail(),
                    googleUser.getGivenName(),
                    googleUser.getFamilyName(),
                    AuthProvider.GOOGLE,
                    googleUser.getId());

            // Remove state from store
            oAuthStateStore.removeState(state);

            log.info("Google OAuth successful for user: {}", googleUser.getEmail());
            response.sendRedirect(oAuthProperties.getFrontendRedirectUrl() + "/api/users/" + userResponse.getId());

        } catch (Exception e) {
            log.error("Error handling Google callback", e);
            oAuthStateStore.removeState(state);
            response.sendRedirect(oAuthProperties.getFrontendRedirectUrl() + "/login?error=google_auth_failed");
        }
    }

    @GetMapping("/github")
    public void initiateGitHubLogin(HttpServletResponse response) throws IOException {
        String state = OAuthUtils.generateState();

        // Store state in memory store (no code verifier for GitHub)
        oAuthStateStore.saveState(state, null);

        String authorizationUrl = gitHubOAuthService.createAuthorizationUrl(state);
        log.debug("Initiating GitHub login with state: {}", state);
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/github/callback")
    public void handleGitHubCallback(
            @RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response)
            throws IOException {

        try {
            log.debug("GitHub callback received with state: {}", state);

            // Validate state from memory store
            if (!oAuthStateStore.isValidState(state)) {
                log.error("Invalid or expired OAuth state: {}", state);
                response.sendRedirect(oAuthProperties.getFrontendRedirectUrl() + "/login?error=invalid_state");
                return;
            }

            OAuthTokenResponse tokens = gitHubOAuthService.exchangeCodeForTokens(code);
            GitHubUserInfo gitHubUser = gitHubOAuthService.getUserInfo(tokens.getAccessToken());
            String email = gitHubOAuthService.getPrimaryEmail(tokens.getAccessToken());

            if (email == null) {
                email = gitHubUser.getEmail();
            }

            String firstName =
                    gitHubUser.getName() != null ? gitHubUser.getName().split(" ")[0] : gitHubUser.getLogin();
            String lastName = gitHubUser.getName() != null
                            && gitHubUser.getName().contains(" ")
                    ? gitHubUser.getName().substring(gitHubUser.getName().indexOf(" ") + 1)
                    : "";

            UserResponse userResponse = userService.processOAuthUser(
                    email, firstName, lastName, AuthProvider.GITHUB, String.valueOf(gitHubUser.getId()));

            // Remove state from store
            oAuthStateStore.removeState(state);

            log.info("GitHub OAuth successful for user: {}", email);
            response.sendRedirect(oAuthProperties.getFrontendRedirectUrl() + "/api/users/" + userResponse.getId());

        } catch (Exception e) {
            log.error("Error handling GitHub callback", e);
            oAuthStateStore.removeState(state);
            response.sendRedirect(oAuthProperties.getFrontendRedirectUrl() + "/login?error=github_auth_failed");
        }
    }

    @GetMapping("/google/url")
    public ResponseEntity<ApiResponse<OAuthUrlResponse>> getGoogleAuthUrl() {
        String state = OAuthUtils.generateState();
        String codeVerifier = OAuthUtils.generateCodeVerifier();

        // Store state and code verifier in memory store
        oAuthStateStore.saveState(state, codeVerifier);

        String authorizationUrl = googleOAuthService.createAuthorizationUrl(state, codeVerifier);
        log.debug("Generated Google auth URL with state: {}", state);

        return ResponseEntity.ok(ApiResponse.success(
                "Google authorization URL generated",
                OAuthUrlResponse.builder().authorizationUrl(authorizationUrl).build()));
    }

    @GetMapping("/github/url")
    public ResponseEntity<ApiResponse<OAuthUrlResponse>> getGitHubAuthUrl() {
        String state = OAuthUtils.generateState();

        // Store state in memory store
        oAuthStateStore.saveState(state, null);

        String authorizationUrl = gitHubOAuthService.createAuthorizationUrl(state);
        log.debug("Generated GitHub auth URL with state: {}", state);

        return ResponseEntity.ok(ApiResponse.success(
                "GitHub authorization URL generated",
                OAuthUrlResponse.builder().authorizationUrl(authorizationUrl).build()));
    }
}
