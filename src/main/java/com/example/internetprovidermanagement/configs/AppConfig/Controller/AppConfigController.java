// AppConfigController.java
package com.example.internetprovidermanagement.configs.AppConfig.Controller;


import com.example.internetprovidermanagement.configs.AppConfig.Service.AppConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class AppConfigController {
    private final AppConfigService configService;

    // Retention Days Endpoints
    @GetMapping("/retention-days")
    public ResponseEntity<Integer> getRetentionDays() {
        return ResponseEntity.ok(configService.getRetentionDays());
    }

    @PutMapping("/retention-days/{days}")
    public ResponseEntity<Void> setRetentionDays(@PathVariable int days) {
        configService.setRetentionDays(days);
        return ResponseEntity.noContent().build();
    }

    // Overdue Processing Endpoints
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
}