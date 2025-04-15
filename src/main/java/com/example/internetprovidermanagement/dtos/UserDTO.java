package com.example.internetprovidermanagement.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(max = 45, message = "First name must be less than 45 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 45, message = "Last name must be less than 45 characters")
    private String lastName;
    
    @Email(message = "Email should be valid")
    @Size(max = 60, message = "Email must be less than 60 characters")
    private String email;
    
    @Size(max = 45, message = "Landline must be less than 45 characters")
    private String landLine;
    
    @NotBlank(message = "Phone number is required")
    @Size(max = 45, message = "Phone number must be less than 45 characters")
    private String phone;

    @NotNull(message = "Subscription date is required")
    private LocalDate subscriptionDate;
    
    private Long locationId;
    private List<UserBundleDTO> bundles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}