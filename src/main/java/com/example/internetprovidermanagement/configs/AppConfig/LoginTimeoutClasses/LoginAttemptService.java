package com.example.internetprovidermanagement.configs.AppConfig.LoginTimeoutClasses;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private final Cache<String, Integer> attemptsCache;
    private final Cache<String, LockoutDetails> lockedIpCache;
    private final LoginAttemptServiceProperties properties;
    private final HttpRequestUtils httpRequestUtils;


    public LoginAttemptService(LoginAttemptServiceProperties properties, HttpRequestUtils httpRequestUtils) {
        this.properties = properties;
        this.httpRequestUtils = httpRequestUtils;

        this.attemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(properties.getAttemptsExpiryHours(), TimeUnit.HOURS)
                .build();
        this.lockedIpCache = Caffeine.newBuilder()
                .expireAfterWrite(properties.getTier3DurationMinutes() + 60, TimeUnit.MINUTES)
                .build();
    }

    public void loginSucceeded(HttpServletRequest request) {
        if (!properties.isEnabled()) return;
        String ip = httpRequestUtils.getClientIpAddress(request);
        attemptsCache.invalidate(ip);
        lockedIpCache.invalidate(ip);
    }

    public void loginFailed(HttpServletRequest request) {
        if (!properties.isEnabled()) return;
        String ip = httpRequestUtils.getClientIpAddress(request);

        if (isIpEffectivelyLocked(ip)) {
            return;
        }
        
        int attempts = attemptsCache.asMap().compute(ip, (k, v) -> (v == null) ? 1 : v + 1);

        long lockoutDurationMinutes = 0;
        String reason = "";

        if (attempts >= properties.getTier3Attempts()) {
            lockoutDurationMinutes = properties.getTier3DurationMinutes();
            reason = "Exceeded tier 3 attempt limit (" + properties.getTier3Attempts() + ")";
        } else if (attempts >= properties.getTier2Attempts()) {
            lockoutDurationMinutes = properties.getTier2DurationMinutes();
            reason = "Exceeded tier 2 attempt limit (" + properties.getTier2Attempts() + ")";
        } else if (attempts >= properties.getTier1Attempts()) {
            lockoutDurationMinutes = properties.getTier1DurationMinutes();
            reason = "Exceeded tier 1 attempt limit (" + properties.getTier1Attempts() + ")";
        }

        if (lockoutDurationMinutes > 0) {
            long expiryTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(lockoutDurationMinutes);
            lockedIpCache.put(ip, new LockoutDetails(expiryTime, reason, lockoutDurationMinutes));
            attemptsCache.invalidate(ip);
        }
    }

    public Optional<LockoutDetails> getLockoutDetails(HttpServletRequest request) {
        if (!properties.isEnabled()) return Optional.empty();
        String ip = httpRequestUtils.getClientIpAddress(request);
        return getLockoutDetailsForIp(ip);
    }
    
    private Optional<LockoutDetails> getLockoutDetailsForIp(String ip) {
        LockoutDetails details = lockedIpCache.getIfPresent(ip);
        if (details == null) {
            return Optional.empty();
        }
        if (System.currentTimeMillis() < details.getExpiryTimeMillis()) {
            return Optional.of(details);
        } else {
            lockedIpCache.invalidate(ip);
            attemptsCache.invalidate(ip);
            return Optional.empty();
        }
    }
    
    private boolean isIpEffectivelyLocked(String ip) {
        LockoutDetails details = lockedIpCache.getIfPresent(ip);
        return details != null && System.currentTimeMillis() < details.getExpiryTimeMillis();
    }


    @Getter
    public static class LockoutDetails {
        private final long expiryTimeMillis;
        private final String reason;
        private final long originalLockoutDurationMinutes;

        public LockoutDetails(long expiryTimeMillis, String reason, long originalLockoutDurationMinutes) {
            this.expiryTimeMillis = expiryTimeMillis;
            this.reason = reason;
            this.originalLockoutDurationMinutes = originalLockoutDurationMinutes;
        }

        public Duration getRemainingLockoutDuration() {
            long remainingMillis = expiryTimeMillis - System.currentTimeMillis();
            return remainingMillis > 0 ? Duration.ofMillis(remainingMillis) : Duration.ZERO;
        }
    }
}