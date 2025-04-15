package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.PaymentDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.PaymentMapper;
import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.repositories.PaymentRepository;
import com.example.internetprovidermanagement.repositories.UserRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository,
                         UserRepository userRepository,
                         PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.paymentMapper = paymentMapper;
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        // Get the user entity from repository
        User user = userRepository.findById(paymentDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + paymentDTO.getUserId()));

        Payment payment = paymentMapper.toPayment(paymentDTO);
        payment.setUser(user); // Set the complete user entity

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentDTO(savedPayment);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return paymentMapper.toPaymentDTO(payment);
    }

    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        
        return paymentRepository.findByUserId(userId).stream()
                .map(paymentMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));

        // Only update user if it's different
        if (!existingPayment.getUser().getId().equals(paymentDTO.getUserId())) {
            User user = userRepository.findById(paymentDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + paymentDTO.getUserId()));
            existingPayment.setUser(user);
        }

        existingPayment.setAmount(paymentDTO.getAmount());
        existingPayment.setPaymentDate(paymentDTO.getPaymentDate());
        existingPayment.setStatus(paymentDTO.getStatus());
        existingPayment.setMethod(paymentDTO.getMethod());
        existingPayment.setDueDate(paymentDTO.getDueDate());

        Payment updatedPayment = paymentRepository.save(existingPayment);
        return paymentMapper.toPaymentDTO(updatedPayment);
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with ID: " + id);
        }
        paymentRepository.deleteById(id);
    }
}