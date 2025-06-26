package com.example.internetprovidermanagement.configs.AppConfig.Service;

import com.example.internetprovidermanagement.dtos.CreatePaymentDTO;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.repositories.PaymentRepository;
import com.example.internetprovidermanagement.repositories.UserBundleRepository;
import com.example.internetprovidermanagement.repositories.UserRepository;
import com.example.internetprovidermanagement.services.PaymentService;
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
    private final AppConfigService configService;
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void dailyPaymentMaintenance() {
        handleOverduePayments();

        if (!configService.isRecurringPaymentsEnabled()) {
            return;
        }
        generateRecurringPayments();
    }


    public void handleOverduePayments() {
        List<Long> overduePaymentIds = paymentRepository.findOverdueActivePaymentIds();
        paymentRepository.bulkMarkAsUnpaid(overduePaymentIds);
        if (!configService.isDisableUnpaidUserbundlesEnabled()) return;
        userBundleRepository.bulkDeactivateBundlesForPayments(overduePaymentIds);
    }


    public void generateRecurringPayments() {
        List<User> activeUsers = userRepository.findAllActiveUsersWithActiveBundles();
        LocalDate today = LocalDate.now();

        activeUsers.forEach(user ->
                user.getBundles().forEach(bundle -> {
                    LocalDate subscriptionDate = bundle.getSubscriptionDate();
                    long daysSinceSubscription = ChronoUnit.DAYS.between(subscriptionDate, today);

                    if (daysSinceSubscription > 0 && daysSinceSubscription % 30 == 0) {
                        CreatePaymentDTO paymentDTO = new CreatePaymentDTO();
                        paymentDTO.setAmount(bundle.getBundle().getPrice());

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