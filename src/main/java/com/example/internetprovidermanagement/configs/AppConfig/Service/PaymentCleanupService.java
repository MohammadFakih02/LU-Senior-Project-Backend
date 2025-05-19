// PaymentCleanupService.java
package com.example.internetprovidermanagement.configs.AppConfig.Service;

import com.example.internetprovidermanagement.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentCleanupService {
    private final PaymentRepository paymentRepository;
    private final AppConfigService configService;

    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void cleanupOldPayments() {
        int retentionDays = configService.getRetentionDays();
        if (retentionDays <= 0) return;

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
        paymentRepository.findPaymentsForCleanup(cutoffDate)
                .forEach(payment -> payment.setDeleted(true));
    }
}