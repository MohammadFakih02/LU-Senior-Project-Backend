package com.example.internetprovidermanagement.dtos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internetprovidermanagement.models.User.UserStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @NotBlank(message = "Phone is required")
    @Size(max = 45, message = "Phone must be less than 45 characters")
    private String phone;

    private BigDecimal consumption = BigDecimal.ZERO;

    @NotNull(message = "Bill is required")
    @DecimalMin(value = "0.0", message = "Bill must be greater than or equal to 0")
    private BigDecimal bill;

    @NotNull(message = "Subscription date is required")
    private LocalDate subscriptionDate;

    private UserStatus status = UserStatus.active;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}