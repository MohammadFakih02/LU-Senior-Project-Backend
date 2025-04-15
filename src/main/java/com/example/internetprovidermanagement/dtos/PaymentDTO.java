package com.example.internetprovidermanagement.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentDTO {
    private Long id;
    
    @NotNull @DecimalMin("0.01")
    private BigDecimal amount;
    
    @NotNull
    private LocalDateTime paymentDate;
    
    @Size(max = 100)
    private String transactionReference;
    
    @Size(max = 50)
    private String paymentMethod;
    
    @NotNull
    private Long userBundleId;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}