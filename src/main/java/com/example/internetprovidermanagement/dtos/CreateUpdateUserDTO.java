// CreateUpdateUserDTO.java
package com.example.internetprovidermanagement.dtos;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.UserBundle;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUpdateUserDTO {
    @NotBlank
    @Size(max = 45)
    private String firstName;
    
    @NotBlank
    @Size(max = 45)
    private String lastName;
    
    @Email
    @Size(max = 60)
    private String email;
    
    @Size(max = 45)
    private String landLine;
    
    @NotBlank
    @Size(max = 45)
    private String phone;
    
    private User.UserStatus status = User.UserStatus.ACTIVE;
    
    @NotNull
    private LocationDTO location;
    
    private Set<UserBundleSubscriptionDTO> bundleSubscriptions = new HashSet<>();
    
    @Data
    public static class UserBundleSubscriptionDTO {
        @NotNull
        private Long bundleId;
        
        @NotNull
        private LocalDate subscriptionDate;
        
        @NotNull
        private UserBundle.BundleStatus status = UserBundle.BundleStatus.ACTIVE;
        
        @NotNull
        private LocationDTO location;
    }
}