package com.example.internetprovidermanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    List<User> findByStatus(User.UserStatus status);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
    @Query("SELECT u FROM User u JOIN FETCH u.location WHERE u.id = :id")
    Optional<User> findByIdWithLocation(Long id);
}