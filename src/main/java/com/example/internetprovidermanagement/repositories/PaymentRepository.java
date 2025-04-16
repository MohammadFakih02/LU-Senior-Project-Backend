package com.example.internetprovidermanagement.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.Payment.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByDueDateBeforeAndStatus(LocalDateTime date, PaymentStatus status);
    
    @Query("SELECT p FROM Payment p JOIN p.userBundle ub JOIN ub.user u WHERE u.id = :userId")
    List<Payment> findByUserId(Long userId);
    
    @Query("SELECT p FROM Payment p JOIN FETCH p.userBundle WHERE p.id = :id")
    Optional<Payment> findByIdWithUserBundle(Long id);
}