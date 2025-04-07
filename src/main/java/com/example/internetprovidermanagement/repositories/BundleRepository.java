package com.example.internetprovidermanagement.repositories;
import com.example.internetprovidermanagement.models.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    // Optional: Find Bundles by type
    List<Bundle> findByType(String type);
}