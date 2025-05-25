package com.example.internetprovidermanagement.configs.AppConfig.Controller;


import com.example.internetprovidermanagement.configs.AppConfig.Service.AppConfigService;
import com.example.internetprovidermanagement.dtos.ChangePasswordDTO; // Import the new DTO
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class AppConfigController {
    private final AppConfigService configService;

    @GetMapping("/retention-days")
    public ResponseEntity<Integer> getRetentionDays() {
        return ResponseEntity.ok(configService.getRetentionDays());
    }

    @PutMapping("/retention-days/{days}")
    public ResponseEntity<Void> setRetentionDays(@PathVariable int days) {
        configService.setRetentionDays(days);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/overdue-processing")
    public ResponseEntity<Boolean> isDisableUnpaidUserbundlesEnabled() {
        return ResponseEntity.ok(configService.isDisableUnpaidUserbundlesEnabled());
    }

    @PutMapping("/overdue-processing/{enabled}")
    public ResponseEntity<Void> setDisableUnpaidUserbundlesEnabled(@PathVariable boolean enabled) {
        configService.setDisableUnpaidUserbundlesEnabled(enabled);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recurring-payments")
    public ResponseEntity<Boolean> isRecurringPaymentsEnabled() {
        return ResponseEntity.ok(configService.isRecurringPaymentsEnabled());
    }

    @PutMapping("/recurring-payments/{enabled}")
    public ResponseEntity<Void> setRecurringPaymentsEnabled(
            @PathVariable boolean enabled
    ) {
        configService.setRecurringPaymentsEnabled(enabled);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/initial-payment/auto-create")
    public ResponseEntity<Boolean> isAutoCreateInitialPaymentEnabled() {
        return ResponseEntity.ok(configService.isAutoCreateInitialPaymentEnabled());
    }

    @PutMapping("/initial-payment/auto-create/{enabled}")
    public ResponseEntity<Void> setAutoCreateInitialPaymentEnabled(@PathVariable boolean enabled) {
        configService.setAutoCreateInitialPaymentEnabled(enabled);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/security/username")
    public ResponseEntity<String> getAdminUsername() {
        return ResponseEntity.ok(configService.getAdminUsername());
    }

    @PutMapping("/security/username")
    public ResponseEntity<Void> setAdminUsername(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        if (username == null) {
            return ResponseEntity.badRequest().build(); // Or throw ValidationException
        }
        configService.setAdminUsername(username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/security/password")
    public ResponseEntity<Void> changeAdminPassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        if (changePasswordDTO == null ||
                changePasswordDTO.getOldPassword() == null ||
                changePasswordDTO.getNewPassword() == null) {
            return ResponseEntity.badRequest().build();
        }
        configService.changeAdminPassword(changePasswordDTO);
        return ResponseEntity.noContent().build();
    }
}