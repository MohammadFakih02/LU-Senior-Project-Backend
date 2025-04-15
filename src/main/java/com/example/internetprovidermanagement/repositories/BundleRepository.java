package com.example.internetprovidermanagement.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.Bundle;

@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {
    List<Bundle> findByType(Bundle.BundleType type); // Add this method
}