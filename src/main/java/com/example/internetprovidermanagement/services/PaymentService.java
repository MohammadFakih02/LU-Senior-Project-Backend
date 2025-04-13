package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.example.internetprovidermanagement.dtos.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.PaymentDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.PaymentMapper;
import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.repositories.PaymentRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository,
                         UserService userService,
                         PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.paymentMapper = paymentMapper;
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        UserDTO userDTO = userService.getUserById(paymentDTO.getUserId());
        User user = new User();
        user.setId(userDTO.getId());

        Payment payment = paymentMapper.toPaymentWithUser(paymentDTO, user);

        if(payment.getPaymentDate() == null) {
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
        return paymentRepository.findByUserId(userId).stream()
                .map(paymentMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));

        if(!existingPayment.getUser().getId().equals(paymentDTO.getUserId())) {
            UserDTO newUserDTO = userService.getUserById(paymentDTO.getUserId());
            User newUser = new User();
            newUser.setId(newUserDTO.getId());
            existingPayment.setUser(newUser);
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
        if(!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with ID: " + id);
        }
        paymentRepository.deleteById(id);
    }
}