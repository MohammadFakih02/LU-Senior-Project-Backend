package com.example.internetprovidermanagement.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {
    private Long id;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 45, message = "City must be less than 45 characters")
    private String city;

    @NotBlank(message = "Street is required")
    @Size(max = 45, message = "Street must be less than 45 characters")
    private String street;

    @NotBlank(message = "Building is required")
    @Size(max = 45, message = "Building must be less than 45 characters")
    private String building;

    @Size(max = 45, message = "Floor must be less than 45 characters")
    private String floor;

    @Size(max = 255, message = "Google Maps URL must be less than 255 characters")
    private String googleMapsUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}