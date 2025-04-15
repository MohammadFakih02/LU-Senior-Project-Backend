package com.example.internetprovidermanagement.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Locations")
@Getter
@Setter
public class Location extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LocationID")
    private Long id;
    
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be less than 255 characters")
    @Column(name = "Address", nullable = false)
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(max = 45, message = "City must be less than 45 characters")
    @Column(name = "City", nullable = false)
    private String city;
    
    @NotBlank(message = "Street is required")
    @Size(max = 45, message = "Street must be less than 45 characters")
    @Column(name = "Street", nullable = false)
    private String street;
    
    @NotBlank(message = "Building is required")
    @Size(max = 45, message = "Building must be less than 45 characters")
    @Column(name = "Building", nullable = false)
    private String building;
    
    @Size(max = 45, message = "Floor must be less than 45 characters")
    @Column(name = "Floor")
    private String floor;
    
    @Size(max = 255, message = "Google Maps URL must be less than 255 characters")
    @Column(name = "google_maps_url")
    private String googleMapsUrl;
}
