package com.example.internetprovidermanagement.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.internetprovidermanagement.dtos.PaymentDTO;
import com.example.internetprovidermanagement.services.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDTO createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return paymentService.createPayment(paymentDTO);
    }

    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public PaymentDTO getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/user/{userId}")
    public List<PaymentDTO> getPaymentsByUser(@PathVariable Long userId) {
        return paymentService.getPaymentsByUserId(userId);
    }

    @PutMapping("/{id}")
    public PaymentDTO updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentDTO paymentDTO) {
        return paymentService.updatePayment(id, paymentDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }
}