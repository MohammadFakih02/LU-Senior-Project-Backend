package com.example.internetprovidermanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.Bundle;

@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {
    boolean existsByName(String name);
}