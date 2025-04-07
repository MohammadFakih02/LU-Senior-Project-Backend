package com.example.internetprovidermanagement.dtos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internetprovidermanagement.models.Payment.PaymentStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    private LocalDate paymentDate;

    @NotNull(message = "Status is required")
    private PaymentStatus status;

    @Size(max = 50, message = "Method must be less than 50 characters")
    private String method;

    @NotNull(message = "User ID is required")
    private Long userId;

    private LocalDate dueDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}