package com.example.internetprovidermanagement.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.internetprovidermanagement.exceptions.InvalidOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.CreatePaymentDTO;
import com.example.internetprovidermanagement.dtos.PaymentResponseDTO;
import com.example.internetprovidermanagement.dtos.UpdatePaymentDTO;
import com.example.internetprovidermanagement.exceptions.PaymentProcessingException;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.PaymentMapper;
import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.UserBundle;
import com.example.internetprovidermanagement.repositories.PaymentRepository;
import com.example.internetprovidermanagement.repositories.UserBundleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserBundleRepository userBundleRepository;
    private final PaymentMapper paymentMapper;

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toPaymentResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponseDTO createPayment(CreatePaymentDTO paymentDTO) {
        UserBundle userBundle = userBundleRepository.findById(paymentDTO.getUserBundleId())
                .orElseThrow(() -> new ResourceNotFoundException("User bundle not found"));
        if (userBundle.isDeleted()) {
            throw new InvalidOperationException("Cannot create payment for deleted user bundle");
        }
        Payment payment = paymentMapper.toPayment(paymentDTO);
        payment.setUserBundle(userBundle);

        if (payment.getPaymentDate() == null && payment.getStatus() == Payment.PaymentStatus.PAID) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        return paymentMapper.toPaymentResponseDTO(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponseDTO updatePayment(Long id, UpdatePaymentDTO paymentDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));

        if (payment.isDeleted()) {
            throw new InvalidOperationException("Cannot update a deleted payment with ID: " + id);
        }

        if (paymentDTO.getAmount() != null) {
            payment.setAmount(paymentDTO.getAmount());
        }
        if (paymentDTO.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        }
        if (paymentDTO.getStatus() != null) {
            payment.setStatus(paymentDTO.getStatus());
            if (paymentDTO.getStatus() == Payment.PaymentStatus.PAID && payment.getPaymentDate() == null) { // Set payment date only if not already set
                payment.setPaymentDate(LocalDateTime.now());
            }
        }

        return paymentMapper.toPaymentResponseDTO(paymentRepository.save(payment));
    }

    @Transactional
    public void processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (payment.isDeleted()) {
            throw new InvalidOperationException("Cannot process a deleted payment with ID: " + paymentId);
        }
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new PaymentProcessingException("Only pending payments can be processed. Payment ID: " + paymentId + " has status: " + payment.getStatus());
        }

        try {
            payment.setStatus(Payment.PaymentStatus.PAID);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
        } catch (Exception e) {
            throw new PaymentProcessingException("Payment processing failed for ID " + paymentId + ": " + e.getMessage());
        }
    }

    @Transactional
    public void softDeletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));

        if (payment.isDeleted()) {
            // Optionally, you can throw an exception or just do nothing if it's already deleted.
            // For idempotency, often doing nothing is fine.
            // throw new InvalidOperationException("Payment with ID: " + id + " is already deleted.");
            return;
        }

        payment.setDeleted(true);
        paymentRepository.save(payment);
    }
}