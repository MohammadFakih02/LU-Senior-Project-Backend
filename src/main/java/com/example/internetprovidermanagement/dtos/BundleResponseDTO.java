// BundleResponseDTO.java (for getAll)
package com.example.internetprovidermanagement.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BundleResponseDTO {
    private Long bundleId;
    private String name;
    private String description;
    private String type;
    private BigDecimal price;
    private Integer dataCap;
    private Integer speed;
}