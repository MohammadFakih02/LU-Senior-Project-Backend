package com.example.internetprovidermanagement.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
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
    @Column(name = "bundle_id")
    private Long bundleId;
    
    @NotBlank(message = "Name is required")
    @Size(max = 45, message = "Name must be less than 45 characters")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(nullable = false)
    private String description;
    
    @NotBlank(message = "Type is required")
    @Size(max = 45, message = "Type must be less than 45 characters")
    @Column(nullable = false)
    private String type;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @Min(value = 0, message = "Data cap cannot be negative")
    @Column(nullable = false)
    private Integer dataCap = 0;
    
    @Min(value = 0, message = "Speed cannot be negative")
    @Column(nullable = false)
    private Integer speed = 0;
}