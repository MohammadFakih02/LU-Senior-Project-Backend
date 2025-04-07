package com.example.internetprovidermanagement.repositories;

import com.example.internetprovidermanagement.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Optional: Add custom query methods here
    User findByEmail(String email); // Example custom query
}