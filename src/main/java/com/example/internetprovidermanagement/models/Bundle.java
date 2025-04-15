package com.example.internetprovidermanagement.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Bundles")
@Getter
@Setter
public class Bundle extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BundleID")
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(max = 45, message = "Name must be less than 45 characters")
    @Column(name = "Name", nullable = false)
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(name = "Description", nullable = false)
    private String description;

    public enum BundleType {
        Fiber, DSL, VDSL
    }

    @NotNull(message = "Type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false)
    private BundleType type;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 2 decimal places")
    @Column(name = "Price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @NotNull(message = "DataCap is required")
    @Min(value = 0, message = "DataCap must be a non-negative integer")
    @Column(name = "data_cap", nullable = false)
    private Integer dataCap;
    
    @NotNull(message = "Speed is required")
    @Min(value = 1, message = "Speed must be a positive integer")
    @Column(name = "Speed", nullable = false)
    private Integer speed;
}
