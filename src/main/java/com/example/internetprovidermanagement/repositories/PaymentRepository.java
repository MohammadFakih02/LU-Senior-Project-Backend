package com.example.internetprovidermanagement.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.internetprovidermanagement.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Optional: Find payments by user ID
    List<Payment> findByUserId(Long userId);
}