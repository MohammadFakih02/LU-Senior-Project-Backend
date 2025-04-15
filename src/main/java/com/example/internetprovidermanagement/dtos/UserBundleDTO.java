package com.example.internetprovidermanagement.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserBundleDTO {
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Bundle ID is required")
    private Long bundleId;
    
    @NotNull(message = "Subscription date is required")
    private LocalDate subscriptionDate;
    
    @NotNull(message = "Status is required")
    private String status;  
    
    private BigDecimal consumption;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Add bundle details for easier display
    private BundleDTO bundle;
    private LocationDTO location;
}