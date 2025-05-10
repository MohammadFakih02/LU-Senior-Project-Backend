package com.example.internetprovidermanagement.models;

import org.hibernate.validator.constraints.URL;

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

import java.util.Objects; // Import Objects

@Entity
@Table(name = "Locations")
@Getter
@Setter
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be less than 255 characters")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 45, message = "City must be less than 45 characters")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Street is required")
    @Size(max = 45, message = "Street must be less than 45 characters")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "Building is required")
    @Size(max = 45, message = "Building must be less than 45 characters")
    @Column(nullable = false)
    private String building;

    @Size(max = 45, message = "Floor must be less than 45 characters")
    private String floor;

    @URL(message = "Must be a valid URL")
    @Size(max = 255, message = "Google Maps URL must be less than 255 characters")
    @Column(name = "google_maps_url")
    private String googleMapsUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Location)) return false;
        Location location = (Location) o;
        // For persisted entities, ID is the defining factor.
        // If locationId is null, it's a transient entity.
        // Two transient entities are only equal if they are the same instance (covered by 'this == o')
        // or if a specific business key equality is defined (not done here for simplicity).
        if (this.locationId == null || location.locationId == null) {
            return false;
        }
        return Objects.equals(locationId, location.locationId);
    }

    @Override
    public int hashCode() {
        // For persisted entities, use the ID. For transient, a constant or System.identityHashCode()
        return locationId != null ? Objects.hashCode(locationId) : System.identityHashCode(this);
    }
}