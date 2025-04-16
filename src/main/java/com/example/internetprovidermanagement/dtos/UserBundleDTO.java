package com.example.internetprovidermanagement.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.internetprovidermanagement.models.UserBundle;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserBundleDTO {
    private Long id;
    
    @NotNull
    private Long userId;
    
    @NotNull
    private Long bundleId;
    
    @NotNull
    private LocalDate subscriptionDate;
    
    private UserBundle.BundleStatus status = UserBundle.BundleStatus.ACTIVE;
    
    @DecimalMin("0.0")
    private BigDecimal consumption = BigDecimal.ZERO;
}