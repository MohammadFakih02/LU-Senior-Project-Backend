// UpdatePaymentDTO.java
package com.example.internetprovidermanagement.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.internetprovidermanagement.models.Payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePaymentDTO {
    @DecimalMin("0.01")
    private BigDecimal amount;
    
    private LocalDateTime paymentDate;
    
    @FutureOrPresent
    private LocalDateTime dueDate;
    
    @Size(max = 50)
    private String paymentMethod;
    
    private Payment.PaymentStatus status;
}