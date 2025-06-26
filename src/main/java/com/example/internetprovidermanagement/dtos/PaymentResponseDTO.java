// PaymentResponseDTO.java
package com.example.internetprovidermanagement.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.internetprovidermanagement.models.Payment;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private Long paymentId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private LocalDateTime dueDate;
    private String paymentMethod;
    private Payment.PaymentStatus status;
    private Long userId;
    private String userName;
    private Long bundleId;
    private String bundleName;
}