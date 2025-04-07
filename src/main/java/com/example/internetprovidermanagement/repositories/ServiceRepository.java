package com.example.internetprovidermanagement.repositories;
import com.example.internetprovidermanagement.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    // Optional: Find services by type
    List<Service> findByType(String type);
}