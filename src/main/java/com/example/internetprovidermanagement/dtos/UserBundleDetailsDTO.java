package com.example.internetprovidermanagement.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserBundleDetailsDTO {
    private Long userBundleId;
    private LocalDate subscriptionDate;
    private String status;
    private BundleResponseDTO bundle;
    private LocationDTO bundleLocation;
    private boolean deleted;
}