package com.example.internetprovidermanagement.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(max = 45, message = "First name must be less than 45 characters")
    @Column(name = "FirstName", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 45, message = "Last name must be less than 45 characters")
    @Column(name = "LastName", nullable = false)
    private String lastName;
    
    @Email(message = "Email should be valid")
    @Size(max = 60, message = "Email must be less than 60 characters")
    @Column(name = "Email", unique = true)
    private String email;
    
    @Size(max = 45, message = "Landline must be less than 45 characters")
    @Column(name = "LandLine")
    private String landLine;
    
    @NotBlank(message = "Phone is required")
    @Size(max = 45, message = "Phone must be less than 45 characters")
    @Column(name = "Phone", nullable = false, unique = true)
    private String phone;
    
    @Column(name = "Consumption", nullable = false, precision = 10, scale = 2)
    private BigDecimal consumption = BigDecimal.ZERO;
    
    @NotNull(message = "Bill is required")
    @DecimalMin(value = "0.0", message = "Bill must be greater than or equal to 0")
    @Column(name = "Bill", nullable = false, precision = 10, scale = 2)
    private BigDecimal bill;
    
    @NotNull(message = "Subscription date is required")
    @Column(name = "SubscriptionDate", nullable = false)
    private LocalDate subscriptionDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private UserStatus status = UserStatus.active;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BundleID", nullable = false)
    private Bundle bundle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LocationID", nullable = false)
    private Location location;

    public enum UserStatus {
        active, inactive, suspended
    }
}