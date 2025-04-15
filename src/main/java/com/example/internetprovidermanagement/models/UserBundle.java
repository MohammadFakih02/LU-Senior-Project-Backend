package com.example.internetprovidermanagement.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UserBundles")
@Getter
@Setter
public class UserBundle extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // Explicit column name
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id", referencedColumnName = "BundleID", nullable = false)  // Explicit column names
    private Bundle bundle;

    @NotNull
    @Column(name = "subscriptionDate")  // Explicit column name
    private LocalDate subscriptionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User.UserStatus status = User.UserStatus.ACTIVE;

    @Column(precision = 10, scale = 2)
    private BigDecimal consumption = BigDecimal.ZERO;

    @OneToMany(mappedBy = "userBundle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();
}