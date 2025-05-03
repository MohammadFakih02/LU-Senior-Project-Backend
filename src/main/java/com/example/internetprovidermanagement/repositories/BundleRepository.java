package com.example.internetprovidermanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.Bundle;

// BundleRepository.java
@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bundle b WHERE b.name = :name AND b.deleted = false")
    boolean existsByName(String name);
}