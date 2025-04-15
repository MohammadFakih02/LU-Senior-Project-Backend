package com.example.internetprovidermanagement.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserBundleDetailsDTO {
    private Long id;
    private LocalDate subscriptionDate;
    private String status;
    private BigDecimal consumption;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BundleDTO bundle;
    private LocationDTO location;
    private List<PaymentDTO> payments;
}