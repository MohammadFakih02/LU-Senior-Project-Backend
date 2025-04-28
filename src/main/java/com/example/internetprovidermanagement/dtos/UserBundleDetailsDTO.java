package com.example.internetprovidermanagement.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserBundleDetailsDTO {
    private Long userBundleId;
    private LocalDate subscriptionDate;
    private String status;
    private BigDecimal consumption;
    private BundleResponseDTO bundle;
    private LocationDTO bundleLocation;
}