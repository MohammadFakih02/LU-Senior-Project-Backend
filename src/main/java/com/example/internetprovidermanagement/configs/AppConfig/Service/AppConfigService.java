package com.example.internetprovidermanagement.configs.AppConfig.Service;

import com.example.internetprovidermanagement.configs.AppConfig.Model.AppConfig;
import com.example.internetprovidermanagement.configs.AppConfig.Repository.AppConfigRepository;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppConfigService {
    private static final String string = "payment.retention.days";
    private final AppConfigRepository configRepository;

    private static final String RETENTION_DAYS_KEY = string;
    private static final String OVERDUE_PROCESSING_KEY = "payment.overdue.enabled";
    private static final String RECURRING_PAYMENTS_KEY = "payment.recurring.enabled";
    private static final String AUTO_CREATE_INITIAL_PAYMENT_KEY = "payment.initial.auto_create.enabled"; // New Key

    @PostConstruct
    void initDefaultConfig() {
        initKey(RETENTION_DAYS_KEY, "60");
        initKey(OVERDUE_PROCESSING_KEY, "true");
        initKey(RECURRING_PAYMENTS_KEY, "true");
        initKey(AUTO_CREATE_INITIAL_PAYMENT_KEY, "true"); // Initialize new key
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
    } //1

    @Transactional
    public void setRetentionDays(int days) {
        validateDays(days);
        saveConfig(RETENTION_DAYS_KEY, String.valueOf(days));
    } //1

    public boolean isDisableUnpaidUserbundlesEnabled() {
        return Boolean.parseBoolean(getConfigValue(OVERDUE_PROCESSING_KEY));
    } //1

    @Transactional
    public void setDisableUnpaidUserbundlesEnabled(boolean enabled) {
        saveConfig(OVERDUE_PROCESSING_KEY, String.valueOf(enabled));
    }//1

    public boolean isRecurringPaymentsEnabled() {
        return Boolean.parseBoolean(getConfigValue(RECURRING_PAYMENTS_KEY));
    }//1

    @Transactional
    public void setRecurringPaymentsEnabled(boolean enabled) {
        saveConfig(RECURRING_PAYMENTS_KEY, String.valueOf(enabled));
    }//1

    // New methods for auto-creating initial payment
    public boolean isAutoCreateInitialPaymentEnabled() {
        return Boolean.parseBoolean(getConfigValue(AUTO_CREATE_INITIAL_PAYMENT_KEY));
    }//1

    @Transactional
    public void setAutoCreateInitialPaymentEnabled(boolean enabled) {
        saveConfig(AUTO_CREATE_INITIAL_PAYMENT_KEY, String.valueOf(enabled));
    }//1
    // End of new methods

    private String getConfigValue(String key) {
        return configRepository.findByConfigKey(key)
                .map(AppConfig::getConfigValue)
                .orElseThrow(() -> new ResourceNotFoundException("Config not found: " + key));
    }//1

    private void saveConfig(String key, String value) {
        AppConfig config = configRepository.findByConfigKey(key)
                .orElseGet(() -> {
                    AppConfig newConfig = new AppConfig();
                    newConfig.setConfigKey(key);
                    return newConfig;
                });
        config.setConfigValue(value);
        configRepository.save(config);
    } //1

    private void validateDays(int days) {
        if (!List.of(0, 30, 60, 90).contains(days)) {
            throw new IllegalArgumentException("Invalid retention days. Allowed: 0, 30, 60, 90");
        }
    } //1
}