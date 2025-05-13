package com.example.internetprovidermanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.bundles ub " + // Fetches all bundles (including deleted)
            "LEFT JOIN FETCH u.location l " +
            "WHERE u.id = :id " +
            "AND u.deleted = false") // Only check user deletion status
    Optional<User> findByIdWithBundlesAndLocation(@Param("id") Long id);

    @Query("SELECT u FROM User u JOIN FETCH u.location WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdWithLocation(Long id);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.bundles ub " +
            "LEFT JOIN FETCH u.location l " +
            "WHERE u.id = :id " +
            "AND u.deleted = false " +
            "AND (ub.deleted = false OR ub IS NULL)")
    Optional<User> findByIdWithActiveBundlesAndLocation(@Param("id") Long id);
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.bundles ub " + // Fetches all bundles (including deleted)
            "WHERE u.deleted = false") // Only filters deleted users
    List<User> findAllActiveUsers();

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.bundles ub " +
            "LEFT JOIN FETCH ub.payments p " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithBundlesAndPayments(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.bundles ub " + // Only fetch users with active bundles
            "WHERE u.deleted = false " +
            "AND ub.deleted = false")
    List<User> findAllActiveUsersWithActiveBundles();

}