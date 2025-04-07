package com.example.internetprovidermanagement.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.internetprovidermanagement.models.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    // Optional: Find locations by city
    List<Location> findByCity(String city);
}