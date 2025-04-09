package com.example.internetprovidermanagement.models;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
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
    @Column(name = "Price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "DataCap", nullable = false, precision = 10, scale = 2)
    private BigDecimal dataCap = BigDecimal.ZERO;
    
    @Column(name = "Speed", nullable = false)
    private Integer speed = 0;
}
