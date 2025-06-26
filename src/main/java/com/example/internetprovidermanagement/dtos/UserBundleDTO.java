package com.example.internetprovidermanagement.dtos;

import java.time.LocalDate;

import com.example.internetprovidermanagement.models.UserBundle;

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
    
    
    @NotNull
    private LocationDTO location;


    private boolean deleted;
}