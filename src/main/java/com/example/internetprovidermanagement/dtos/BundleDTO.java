package com.example.internetprovidermanagement.dtos;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.internetprovidermanagement.models.Bundle;
import jakarta.validation.constraints.DecimalMin;
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

    @NotBlank(message = "Type is required")
    @Size(max = 45, message = "Type must be less than 45 characters")
    private Bundle.BundleType type;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "DataCap is required")
    private BigDecimal dataCap = BigDecimal.ZERO;

    @NotNull(message = "Speed is required")
    private Integer speed = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}