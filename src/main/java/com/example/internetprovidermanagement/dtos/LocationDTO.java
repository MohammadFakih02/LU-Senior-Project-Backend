package com.example.internetprovidermanagement.dtos;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationDTO {
    private Long locationId;
    
    @NotBlank
    @Size(max = 255)
    private String address;
    
    @NotBlank
    @Size(max = 45)
    private String city;
    
    @NotBlank
    @Size(max = 45)
    private String street;
    
    @NotBlank
    @Size(max = 45)
    private String building;
    
    @Size(max = 45)
    private String floor;
    
    @URL
    @Size(max = 255)
    private String googleMapsUrl;
}