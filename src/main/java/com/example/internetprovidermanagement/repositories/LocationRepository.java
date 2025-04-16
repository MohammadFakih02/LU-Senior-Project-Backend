package com.example.internetprovidermanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}