package com.example.internetprovidermanagement.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BundleDTO {
    private Long bundleId;
    
    @NotBlank(message = "Name is required")
    @Size(max = 45)
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(max = 500)
    private String description;
    
    @NotBlank(message = "Type is required")
    @Size(max = 45)
    private String type;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Min(value = 0, message = "Data cap cannot be negative")
    private Integer dataCap = 0;
    
    @Min(value = 0, message = "Speed cannot be negative")
    private Integer speed = 0;

    private boolean deleted;
}