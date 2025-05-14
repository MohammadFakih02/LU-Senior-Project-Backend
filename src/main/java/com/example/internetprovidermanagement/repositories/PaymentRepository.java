package com.example.internetprovidermanagement.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.Payment.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.deleted = false")
    List<Payment> findByStatus(@Param("status") PaymentStatus status);
    List<Payment> findByDueDateBeforeAndStatus(LocalDateTime date, PaymentStatus status);
    
    @Query("SELECT p FROM Payment p JOIN p.userBundle ub JOIN ub.user u WHERE u.id = :userId")
    List<Payment> findByUserId(Long userId);
    
    @Query("SELECT p FROM Payment p JOIN FETCH p.userBundle WHERE p.id = :id")
    Optional<Payment> findByIdWithUserBundle(Long id);

    @Query("SELECT p FROM Payment p WHERE p.deleted = false")
    List<Payment> findAll();

    @Query("SELECT p FROM Payment p " +
            "WHERE p.status = 'PAID' " +
            "AND p.deleted = false " +
            "AND p.paymentDate < :cutoffDate")
    List<Payment> findPaymentsForCleanup(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Query("UPDATE Payment p SET p.deleted = true WHERE p.userBundle IN (SELECT ub FROM UserBundle ub WHERE ub.bundle.bundleId = :bundleId)")
    void softDeleteByBundleId(@Param("bundleId") Long bundleId);

    @Query("SELECT p FROM Payment p " +
            "WHERE p.status NOT IN ('PAID') " +
            "AND p.dueDate < CURRENT_TIMESTAMP " +
            "AND p.deleted = false " +
            "AND p.userBundle.deleted = false " +
            "AND p.userBundle.status = 'ACTIVE'")
    List<Payment> findOverdueActivePayments();

    @Query("SELECT p.id FROM Payment p " +
            "WHERE p.status = 'PENDING' " +
            "AND p.dueDate < CURRENT_TIMESTAMP " +
            "AND p.deleted = false " +
            "AND p.userBundle.deleted = false " +
            "AND p.userBundle.status = 'ACTIVE'")
    List<Long> findOverdueActivePaymentIds();

    @Modifying
    @Query("UPDATE Payment p SET p.status = 'UNPAID' WHERE p.id IN :paymentIds")
    void bulkMarkAsUnpaid(@Param("paymentIds") List<Long> paymentIds);
}