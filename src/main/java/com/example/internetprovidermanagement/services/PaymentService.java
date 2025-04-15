package com.example.internetprovidermanagement.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.PaymentDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.PaymentMapper;
import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.UserBundle;
import com.example.internetprovidermanagement.repositories.PaymentRepository;
import com.example.internetprovidermanagement.repositories.UserBundleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserBundleRepository userBundleRepository;
    private final PaymentMapper paymentMapper;

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        UserBundle userBundle = userBundleRepository.findById(paymentDTO.getUserBundleId())
                .orElseThrow(() -> new ResourceNotFoundException("UserBundle not found with ID: " + paymentDTO.getUserBundleId()));

        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment.setUserBundle(userBundle);
        
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return paymentMapper.toDto(payment);
    }

    public List<PaymentDTO> getPaymentsByUserBundleId(Long userBundleId) {
        if (!userBundleRepository.existsById(userBundleId)) {
            throw new ResourceNotFoundException("UserBundle not found with ID: " + userBundleId);
        }
        
        return paymentRepository.findByUserBundleId(userBundleId).stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));

        if (!existingPayment.getUserBundle().getId().equals(paymentDTO.getUserBundleId())) {
            UserBundle userBundle = userBundleRepository.findById(paymentDTO.getUserBundleId())
                    .orElseThrow(() -> new ResourceNotFoundException("UserBundle not found with ID: " + paymentDTO.getUserBundleId()));
            existingPayment.setUserBundle(userBundle);
        }

        paymentMapper.updatePaymentFromDto(paymentDTO, existingPayment);
        Payment updatedPayment = paymentRepository.save(existingPayment);
        return paymentMapper.toDto(updatedPayment);
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with ID: " + id);
        }
        paymentRepository.deleteById(id);
    }
}