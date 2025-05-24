package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.CreatePaymentDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.exceptions.InvalidOperationException;
import com.example.internetprovidermanagement.exceptions.OperationFailedException;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.exceptions.ValidationException;
import com.example.internetprovidermanagement.mappers.UserBundleMapper;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.UserBundle;
import com.example.internetprovidermanagement.repositories.BundleRepository;
import com.example.internetprovidermanagement.repositories.PaymentRepository;
import com.example.internetprovidermanagement.repositories.UserBundleRepository;
import com.example.internetprovidermanagement.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBundleService {

    private final UserBundleRepository userBundleRepository;
    private final UserRepository userRepository;
    private final BundleRepository bundleRepository;
    private final UserBundleMapper userBundleMapper;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    @SuppressWarnings("UseSpecificCatch")
    public List<UserBundleDetailsDTO> getUserBundles(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }

        try {
            if (!userRepository.existsById(userId)) {
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }

            return userBundleRepository.findByUserIdWithBundle(userId).stream()
                    .map(userBundleMapper::toUserBundleDetailsDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ValidationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to retrieve bundles for user with id: " + userId, ex);
            }
        }
    } //1

    @Transactional
    @SuppressWarnings("UseSpecificCatch")
    public UserBundleDetailsDTO updateUserBundle(Long id, UserBundleDTO userBundleDTO) {
        if (id == null) {
            throw new ValidationException("User bundle ID cannot be null");
        }
        if (userBundleDTO == null) {
            throw new ValidationException("User bundle data cannot be null");
        }

        try {
            UserBundle userBundle = userBundleRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User bundle not found with id: " + id));
            if (userBundle.isDeleted()) {
                throw new InvalidOperationException("Cannot update a deleted user bundle");
            }

            if (userBundleDTO.getBundleId() != null &&
                    !userBundle.getBundle().getBundleId().equals(userBundleDTO.getBundleId())) {

                Bundle newBundle = bundleRepository.findById(userBundleDTO.getBundleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + userBundleDTO.getBundleId()));

                if (userBundle.getStatus() == UserBundle.BundleStatus.ACTIVE) {
                    throw new InvalidOperationException("Cannot change bundle for an active subscription via this method. Consider deactivating and creating a new one or a dedicated 'change bundle' feature.");
                }

                userBundle.setBundle(newBundle);
            }

            if (userBundleDTO.getStatus() != null) {
                UserBundle.BundleStatus newStatus = userBundleDTO.getStatus();
                if (userBundle.getStatus() == UserBundle.BundleStatus.INACTIVE && newStatus == UserBundle.BundleStatus.ACTIVE) {
                    if (paymentRepository.existsUnpaidPaymentForUserBundle(userBundle.getId())) {
                        throw new InvalidOperationException(
                                "Cannot reactivate UserBundle (ID: " + userBundle.getId() +
                                        ") because it has unpaid payments. Please resolve payments first."
                        );
                    }
                }
                userBundle.setStatus(newStatus);
            }


            // Apply subscriptionDate from DTO if provided
            if (userBundleDTO.getSubscriptionDate() != null) {
                userBundle.setSubscriptionDate(userBundleDTO.getSubscriptionDate());
            }


            return userBundleMapper.toUserBundleDetailsDTO(userBundleRepository.save(userBundle));
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ValidationException || ex instanceof InvalidOperationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to update user bundle with id: " + id, ex);
            }
        }
    }

    @Transactional
    public void softDeleteUserBundle(Long id) {
        UserBundle userBundle = userBundleRepository.findByIdWithPayments(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserBundle not found with ID: " + id));

        if (userBundle.isDeleted()) {
            return;
        }

        userBundle.setDeleted(true);
        userBundle.setStatus(UserBundle.BundleStatus.INACTIVE);

        userBundle.getPayments().forEach(payment -> {
            if (!payment.isDeleted()) {
                payment.setDeleted(true);
            }
        });

        userBundleRepository.save(userBundle);
    }


    @Transactional
    public UserBundleDetailsDTO renewSubscription(Long userBundleId) {
        UserBundle userBundle = userBundleRepository.findById(userBundleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserBundle not found with ID: " + userBundleId));

        if (userBundle.isDeleted()) {
            throw new InvalidOperationException("Cannot renew a deleted UserBundle (ID: " + userBundleId + ").");
        }

        if (userBundle.getUser().isDeleted()) {
            throw new InvalidOperationException("Cannot renew UserBundle (ID: " + userBundleId + ") for a deleted user.");
        }

        UserBundle.BundleStatus oldStatus = userBundle.getStatus();

        // *** CRITICAL LINES FOR DATE UPDATE ***
        userBundle.setSubscriptionDate(LocalDate.now());
        userBundle.setStatus(UserBundle.BundleStatus.ACTIVE);

        if (oldStatus == UserBundle.BundleStatus.INACTIVE) {
            if (paymentRepository.existsUnpaidPaymentForUserBundle(userBundle.getId())) {
                throw new InvalidOperationException(
                        "Cannot renew and activate UserBundle (ID: " + userBundleId +
                                ") because it has outstanding unpaid payments. Please resolve them first."
                );
            }
        }

        // *** CRITICAL SAVE OPERATION ***
        UserBundle savedUserBundle = userBundleRepository.save(userBundle);

        // Create a new pending payment for the renewal
        CreatePaymentDTO paymentDTO = new CreatePaymentDTO();
        paymentDTO.setAmount(savedUserBundle.getBundle().getPrice());
        paymentDTO.setDueDate(LocalDate.now().plusDays(30).atStartOfDay());
        paymentDTO.setPaymentMethod("Subscription Renewal");
        paymentDTO.setUserBundleId(savedUserBundle.getId());

        paymentService.createPayment(paymentDTO);

        return userBundleMapper.toUserBundleDetailsDTO(savedUserBundle);
    } //1
}