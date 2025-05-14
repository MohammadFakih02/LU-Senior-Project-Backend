package com.example.internetprovidermanagement.services;

import com.example.internetprovidermanagement.dtos.CreatePaymentDTO;
import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.UserBundle;
import com.example.internetprovidermanagement.repositories.PaymentRepository;
import com.example.internetprovidermanagement.repositories.UserBundleRepository;
import com.example.internetprovidermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentSchedulerService {
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final UserBundleRepository userBundleRepository;
    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void dailyPaymentMaintenance() {
        // 1. Process overdue payments
        handleOverduePayments();

        // 2. Generate new payments
        generateRecurringPayments();
    }


    private void handleOverduePayments() {
        // Get overdue payments without loading full entities
        List<Long> overduePaymentIds = paymentRepository.findOverdueActivePaymentIds();

        // Bulk update payment statuses
        paymentRepository.bulkMarkAsUnpaid(overduePaymentIds);

        // Bulk deactivate user bundles
        userBundleRepository.bulkDeactivateBundlesForPayments(overduePaymentIds);
    }

        // 2. Then generate new payments


     // Runs daily at midnight

    public void generateRecurringPayments() {
        List<User> activeUsers = userRepository.findAllActiveUsersWithActiveBundles();
        LocalDate today = LocalDate.now();

        activeUsers.forEach(user ->
                user.getBundles().forEach(bundle -> {
                    LocalDate subscriptionDate = bundle.getSubscriptionDate();
                    long daysSinceSubscription = ChronoUnit.DAYS.between(subscriptionDate, today);

                    // Check if it's a 30-day interval and not the subscription day itself
                    if (daysSinceSubscription > 0 && daysSinceSubscription % 30 == 0) {
                        CreatePaymentDTO paymentDTO = new CreatePaymentDTO();
                        paymentDTO.setAmount(bundle.getBundle().getPrice());

                        // Calculate due date as next interval (e.g., day 30 → due on day 60)
                        long periodsPassed = daysSinceSubscription / 30;
                        LocalDate dueDate = subscriptionDate.plusDays(30 * (periodsPassed + 1));

                        paymentDTO.setDueDate(dueDate.atStartOfDay());
                        paymentDTO.setPaymentMethod("Auto-generated");
                        paymentDTO.setUserBundleId(bundle.getId());

                        paymentService.createPayment(paymentDTO);
                    }
                })
        );
    }
}