package com.example.internetprovidermanagement.dtos;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.internetprovidermanagement.models.Bundle;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BundleDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 45, message = "Name must be less than 45 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Type is required")
    private Bundle.BundleType type;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "DataCap is required")
    @Min(value = 0, message = "DataCap must be a non-negative integer")
    private Integer dataCap;

    @NotNull(message = "Speed is required")
    @Min(value = 1, message = "Speed must be a positive integer")
    private Integer speed;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}