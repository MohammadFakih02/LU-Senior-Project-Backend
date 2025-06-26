package com.example.internetprovidermanagement.configs.AppConfig.LoginTimeoutClasses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "login.lockout")
@Getter
@Setter
public class LoginAttemptServiceProperties {
    private boolean enabled = true;
    private int tier1Attempts = 5;
    private int tier1DurationMinutes = 5;
    private int tier2Attempts = 10;
    private int tier2DurationMinutes = 10;
    private int tier3Attempts = 15;
    private int tier3DurationMinutes = 30;
    private int attemptsExpiryHours = 1;
}