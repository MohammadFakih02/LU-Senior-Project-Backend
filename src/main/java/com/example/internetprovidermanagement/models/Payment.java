package com.example.internetprovidermanagement.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payments")
@Getter
@Setter
public class Payment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be in the present or future")
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;
    
    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "Payment method must be less than 50 characters")
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @NotNull(message = "User bundle is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_bundle_id", nullable = false)
    private UserBundle userBundle;
}