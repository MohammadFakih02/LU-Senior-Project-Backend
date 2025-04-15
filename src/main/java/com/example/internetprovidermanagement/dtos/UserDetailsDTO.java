package com.example.internetprovidermanagement.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internetprovidermanagement.models.User.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String landLine;
    private String phone;
    private BigDecimal consumption;
    private BigDecimal bill;
    private LocalDate subscriptionDate;
    private UserStatus status;
    private BundleDTO bundle;
    private LocationDTO location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}