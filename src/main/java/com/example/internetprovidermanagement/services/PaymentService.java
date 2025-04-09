package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.example.internetprovidermanagement.dtos.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.PaymentDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.repositories.PaymentRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final BundleService bundleService;
    private final LocationService locationService;
    private final ModelMapper modelMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          UserService userService,
                          BundleService bundleService,
                          LocationService locationService,
                          ModelMapper modelMapper) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.bundleService = bundleService;
        this.locationService = locationService;
        this.modelMapper = modelMapper;
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        // Resolve full user entity with relationships
        UserDTO userDTO = userService.getUserById(paymentDTO.getUserId());
        Bundle bundle = modelMapper.toBundle(bundleService.getBundleById(userDTO.getBundleId()));
        Location location = modelMapper.toLocation(locationService.getLocationById(userDTO.getLocationId()));
        User user = modelMapper.toUser(userDTO, bundle, location);

        Payment payment = modelMapper.toPayment(paymentDTO, user);

        // Set default payment date if not provided
        if(payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return modelMapper.toPaymentDTO(savedPayment);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(modelMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return modelMapper.toPaymentDTO(payment);
    }

    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(modelMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));

        // Update user relationship if changed
        if(!existingPayment.getUser().getId().equals(paymentDTO.getUserId())) {
            UserDTO newUserDTO = userService.getUserById(paymentDTO.getUserId());
            Bundle newBundle = modelMapper.toBundle(bundleService.getBundleById(newUserDTO.getBundleId()));
            Location newLocation = modelMapper.toLocation(locationService.getLocationById(newUserDTO.getLocationId()));
            User newUser = modelMapper.toUser(newUserDTO, newBundle, newLocation);
            existingPayment.setUser(newUser);
        }

        // Update other fields
        existingPayment.setAmount(paymentDTO.getAmount());
        existingPayment.setPaymentDate(paymentDTO.getPaymentDate());
        existingPayment.setStatus(paymentDTO.getStatus());
        existingPayment.setMethod(paymentDTO.getMethod());
        existingPayment.setDueDate(paymentDTO.getDueDate());

        Payment updatedPayment = paymentRepository.save(existingPayment);
        return modelMapper.toPaymentDTO(updatedPayment);
    }

    public void deletePayment(Long id) {
        if(!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with ID: " + id);
        }
        paymentRepository.deleteById(id);
    }
}