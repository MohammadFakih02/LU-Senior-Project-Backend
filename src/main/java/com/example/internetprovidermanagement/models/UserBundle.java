// UserBundle.java (updated)
package com.example.internetprovidermanagement.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.CascadeType;
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
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UserBundles")
@Getter
@Setter
public class UserBundle extends BaseEntity {
    
    public enum BundleStatus {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull(message = "Bundle is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id", nullable = false)
    private Bundle bundle;
    
    @NotNull(message = "Subscription date is required")
    @Column(name = "subscription_date", nullable = false)
    private LocalDate subscriptionDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable = false)
    private BundleStatus status = BundleStatus.ACTIVE;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    @DecimalMin(value = "0.0", message = "Consumption cannot be negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal consumption = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean deleted = false;


}