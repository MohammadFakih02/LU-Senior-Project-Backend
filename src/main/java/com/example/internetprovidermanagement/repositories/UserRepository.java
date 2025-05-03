package com.example.internetprovidermanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.User;

// UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = false")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.deleted = false")
    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.deleted = false")
    List<User> findByStatus(User.UserStatus status);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.deleted = false")
    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phone = :phone AND u.deleted = false")
    boolean existsByPhone(String phone);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.bundles LEFT JOIN FETCH u.location WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdWithBundlesAndLocation(Long id);

    @Query("SELECT u FROM User u JOIN FETCH u.location WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdWithLocation(Long id);
}