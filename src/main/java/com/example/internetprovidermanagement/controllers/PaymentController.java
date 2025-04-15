package com.example.internetprovidermanagement.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.internetprovidermanagement.dtos.PaymentDTO;
import com.example.internetprovidermanagement.services.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(paymentDTO));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/user-bundle/{userBundleId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUserBundle(
            @PathVariable Long userBundleId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserBundleId(userBundleId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(paymentService.updatePayment(id, paymentDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }
}