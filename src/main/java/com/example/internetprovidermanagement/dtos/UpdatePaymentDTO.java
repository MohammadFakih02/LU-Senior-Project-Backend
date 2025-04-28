// UpdatePaymentDTO.java
package com.example.internetprovidermanagement.dtos;

import com.example.internetprovidermanagement.models.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePaymentDTO {
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    @Size(max = 50, message = "Payment method must be less than 50 characters")
    private String paymentMethod;  // Removed @NotBlank

    private Payment.PaymentStatus status;  // Removed @NotNull
}