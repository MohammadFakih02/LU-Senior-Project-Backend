package com.example.internetprovidermanagement.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // Only update what's provided in DTO
        if (paymentDTO.getAmount() != null) {
            payment.setAmount(paymentDTO.getAmount());
        }
        if (paymentDTO.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        }
        if (paymentDTO.getStatus() != null) {
            payment.setStatus(paymentDTO.getStatus());
            // Auto-set payment date only if status changes to PAID
            if (paymentDTO.getStatus() == Payment.PaymentStatus.PAID) {
                payment.setPaymentDate(LocalDateTime.now());
            }
        }

        return paymentMapper.toPaymentResponseDTO(paymentRepository.save(payment));
    }

    @Transactional
    public void processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new PaymentProcessingException("Only pending payments can be processed");
        }

        // Simulate payment processing
        try {
            payment.setStatus(Payment.PaymentStatus.PAID);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
        } catch (Exception e) {
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage());
        }
    }
}