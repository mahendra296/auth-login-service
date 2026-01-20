package com.auth.service.oauth;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuthStateStore {

    private final Map<String, OAuthStateData> store = new ConcurrentHashMap<>();

    private static final long EXPIRY_MINUTES = 10;

    public void saveState(String state, String codeVerifier) {
        store.put(state, new OAuthStateData(codeVerifier, Instant.now()));
        log.debug("Saved OAuth state: {}", state);
    }

    public String getCodeVerifier(String state) {
        OAuthStateData data = store.get(state);
        if (data == null) {
            log.debug("OAuth state not found: {}", state);
            return null;
        }
        return data.codeVerifier();
    }

    public boolean isValidState(String state) {
        OAuthStateData data = store.get(state);
        if (data == null) {
            return false;
        }
        // Check if expired
        if (data.createdAt().plusSeconds(EXPIRY_MINUTES * 60).isBefore(Instant.now())) {
            store.remove(state);
            return false;
        }
        return true;
    }

    public void removeState(String state) {
        store.remove(state);
        log.debug("Removed OAuth state: {}", state);
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void cleanupExpiredStates() {
        Instant expiryThreshold = Instant.now().minusSeconds(EXPIRY_MINUTES * 60);
        store.entrySet().removeIf(entry -> entry.getValue().createdAt().isBefore(expiryThreshold));
    }

    private record OAuthStateData(String codeVerifier, Instant createdAt) {}
}
