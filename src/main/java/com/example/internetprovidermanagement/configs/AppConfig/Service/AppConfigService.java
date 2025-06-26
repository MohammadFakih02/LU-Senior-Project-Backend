package com.example.internetprovidermanagement.configs.AppConfig.Service;

import com.example.internetprovidermanagement.configs.AppConfig.Model.AppConfig;
import com.example.internetprovidermanagement.configs.AppConfig.Repository.AppConfigRepository;
import com.example.internetprovidermanagement.dtos.ChangePasswordDTO; // Import the new DTO
import com.example.internetprovidermanagement.exceptions.BadRequestException; // For validation errors
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppConfigService {
    private final AppConfigRepository configRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private static final String RETENTION_DAYS_KEY = "payment.retention.days";
    private static final String OVERDUE_PROCESSING_KEY = "payment.overdue.enabled";
    private static final String RECURRING_PAYMENTS_KEY = "payment.recurring.enabled";
    private static final String AUTO_CREATE_INITIAL_PAYMENT_KEY = "payment.initial.auto_create.enabled";

    public static final String SECURITY_ADMIN_USERNAME_KEY = "security.admin.username";
    public static final String SECURITY_ADMIN_PASSWORD_KEY = "security.admin.password";

    @PostConstruct
    @Transactional
    void initDefaultConfig() {
        initKey(RETENTION_DAYS_KEY, "60");
        initKey(OVERDUE_PROCESSING_KEY, "true");
        initKey(RECURRING_PAYMENTS_KEY, "true");
        initKey(AUTO_CREATE_INITIAL_PAYMENT_KEY, "true");

        initKey(SECURITY_ADMIN_USERNAME_KEY, "admin");
        if (passwordEncoder != null) {
            if (!configRepository.existsByConfigKey(SECURITY_ADMIN_PASSWORD_KEY)) {
                AppConfig passwordConfig = new AppConfig();
                passwordConfig.setConfigKey(SECURITY_ADMIN_PASSWORD_KEY);
                passwordConfig.setConfigValue(passwordEncoder.encode("admin123"));
                configRepository.save(passwordConfig);
            }
        } else {
            initKey(SECURITY_ADMIN_PASSWORD_KEY, "$2a$10$CHANGEMECHANGEMECHANGEMECHANGEMEU");
        }
    }

    private void initKey(String key, String defaultValue) {
        if (!configRepository.existsByConfigKey(key)) {
            AppConfig config = new AppConfig();
            config.setConfigKey(key);
            config.setConfigValue(defaultValue);
            configRepository.save(config);
        }
    }

    public int getRetentionDays() {
        return Integer.parseInt(getConfigValue(RETENTION_DAYS_KEY));
    }

    @Transactional
    public void setRetentionDays(int days) {
        validateDays(days);
        saveConfig(RETENTION_DAYS_KEY, String.valueOf(days));
    }

    public boolean isDisableUnpaidUserbundlesEnabled() {
        return Boolean.parseBoolean(getConfigValue(OVERDUE_PROCESSING_KEY));
    }

    @Transactional
    public void setDisableUnpaidUserbundlesEnabled(boolean enabled) {
        saveConfig(OVERDUE_PROCESSING_KEY, String.valueOf(enabled));
    }

    public boolean isRecurringPaymentsEnabled() {
        return Boolean.parseBoolean(getConfigValue(RECURRING_PAYMENTS_KEY));
    }

    @Transactional
    public void setRecurringPaymentsEnabled(boolean enabled) {
        saveConfig(RECURRING_PAYMENTS_KEY, String.valueOf(enabled));
    }

    public boolean isAutoCreateInitialPaymentEnabled() {
        return Boolean.parseBoolean(getConfigValue(AUTO_CREATE_INITIAL_PAYMENT_KEY));
    }

    @Transactional
    public void setAutoCreateInitialPaymentEnabled(boolean enabled) {
        saveConfig(AUTO_CREATE_INITIAL_PAYMENT_KEY, String.valueOf(enabled));
    }

    public String getAdminUsername() {
        return getConfigValue(SECURITY_ADMIN_USERNAME_KEY);
    }

    @Transactional
    public void setAdminUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin username cannot be empty.");
        }
        saveConfig(SECURITY_ADMIN_USERNAME_KEY, username);
    }

    public String getAdminPasswordHash() {
        return getConfigValue(SECURITY_ADMIN_PASSWORD_KEY);
    }

    @Transactional
    public void changeAdminPassword(ChangePasswordDTO changePasswordDTO) {
        if (changePasswordDTO == null ||
                changePasswordDTO.getOldPassword() == null || changePasswordDTO.getOldPassword().isEmpty() ||
                changePasswordDTO.getNewPassword() == null || changePasswordDTO.getNewPassword().isEmpty()) {
            throw new BadRequestException("Old password and new password must be provided.");
        }

        if (passwordEncoder == null) {
            throw new IllegalStateException("PasswordEncoder not initialized. Cannot change password.");
        }

        String currentPasswordHash = getAdminPasswordHash();

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), currentPasswordHash)) {
            throw new BadRequestException("Incorrect old password.");
        }

        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new BadRequestException("New password cannot be the same as the old password.");
        }
        saveConfig(SECURITY_ADMIN_PASSWORD_KEY, passwordEncoder.encode(changePasswordDTO.getNewPassword()));
    }


    private String getConfigValue(String key) {
        return configRepository.findByConfigKey(key)
                .map(AppConfig::getConfigValue)
                .orElseThrow(() -> new ResourceNotFoundException("Config not found: " + key));
    }

    private void saveConfig(String key, String value) {
        AppConfig config = configRepository.findByConfigKey(key)
                .orElseGet(() -> {
                    AppConfig newConfig = new AppConfig();
                    newConfig.setConfigKey(key);
                    return newConfig;
                });
        config.setConfigValue(value);
        configRepository.save(config);
    }

    private void validateDays(int days) {
        if (!List.of(0, 30, 60, 90).contains(days)) {
            throw new IllegalArgumentException("Invalid retention days. Allowed: 0, 30, 60, 90");
        }
    }
}