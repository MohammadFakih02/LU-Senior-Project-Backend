package com.example.internetprovidermanagement.services;

import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCleanupService {
    private final PaymentRepository paymentRepository;

    @Scheduled(cron = "0 0 0 * * *") // Midnight daily
    @Transactional
    public void cleanupOldPayments() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(60);
        List<Payment> oldPayments = paymentRepository.findPaymentsForCleanup(cutoffDate);

        oldPayments.forEach(payment -> {
            payment.setDeleted(true);
            paymentRepository.save(payment);
        });
    }
}